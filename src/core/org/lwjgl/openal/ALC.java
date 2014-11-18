/*
 * Copyright LWJGL. All rights reserved.
 * License terms: http://lwjgl.org/license.php
 */
package org.lwjgl.openal;

import org.lwjgl.LWJGLUtil;
import org.lwjgl.system.APIBuffer;
import org.lwjgl.system.DynamicLinkLibrary;
import org.lwjgl.system.FunctionProviderLocal;

import java.nio.ByteBuffer;
import java.util.*;

import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.system.APIUtil.*;
import static org.lwjgl.system.Checks.*;
import static org.lwjgl.system.MemoryUtil.*;

public final class ALC {

	static final FunctionProviderLocal functionProvider;

	static {
		final String libName;
		final String libNamePlatform;
		switch ( LWJGLUtil.getPlatform() ) {
			case WINDOWS:
				libName = "OpenAL32";
				libNamePlatform = "OpenAL32.dll";
				break;
			case LINUX:
				libName = "openal";
				libNamePlatform = "libopenal.so";
				break;
			case MACOSX:
				libName = "openal";
				// TODO: Support .framework too?
				libNamePlatform = "libopenal.dylib";
				break;
			default:
				throw new IllegalStateException();
		}

		functionProvider = new FunctionProviderLocal.Default() {

			private final DynamicLinkLibrary OPENAL;
			private final long alcGetProcAddress;

			{
				String[] paths = LWJGLUtil.getLibraryPaths(ALC.class.getClassLoader(), libName, libNamePlatform);

				DynamicLinkLibrary lib = null;
				for ( String path : paths ) {
					try {
						lib = apiCreateLibrary(path);
						break;
					} catch (Exception e) {
						LWJGLUtil.log("Failed to load " + path + ": " + e.getMessage());
					}
				}
				if ( lib == null )
					throw new RuntimeException("Failed to locate the OpenAL library");

				OPENAL = lib;

				alcGetProcAddress = getFunctionAddress("alcGetProcAddress");
				if ( alcGetProcAddress == NULL ) {
					lib.release();
					throw new RuntimeException("A core ALC function is missing. Make sure that OpenAL has been loaded.");
				}
			}

			@Override
			public long getFunctionAddress(CharSequence functionName) {
				long address = OPENAL.getFunctionAddress(functionName);
				if ( address == NULL )
					LWJGLUtil.log("Failed to locate address for ALC function " + functionName);

				return address;
			}

			@Override
			public long getFunctionAddress(long handle, CharSequence functionName) {
				ByteBuffer nameBuffer = memEncodeASCII(functionName);
				long address = nalcGetProcAddress(handle, memAddress(nameBuffer), alcGetProcAddress);
				if ( address == NULL )
					LWJGLUtil.log("Failed to locate address for ALC extension function " + functionName);

				return address;
			}

			@Override
			protected void destroy() {
				OPENAL.release();
			}
		};
	}

	private static ALCContext context;

	private ALC() {}

	public static FunctionProviderLocal getFunctionProvider() {
		return functionProvider;
	}

	/**
	 * Creates an ALCContext from the specified device. The device name may be null,
	 * in which case the default device will be used.
	 *
	 * @param deviceName the device to open
	 *
	 * @return the created ALCContext on the specified device
	 */
	public static ALCContext createALCContextFromDevice(String deviceName) {
		long alcOpenDevice = functionProvider.getFunctionAddress("alcOpenDevice");
		if ( LWJGLUtil.CHECKS )
			checkFunctionAddress(alcOpenDevice);

		ByteBuffer nameBuffer = deviceName == null ? null : memEncodeUTF8(deviceName);
		long device = nalcOpenDevice(memAddressSafe(nameBuffer), alcOpenDevice);
		if ( device == NULL )
			throw new RuntimeException("Failed to open the device.");

		return new ALCContext(device);
	}

	static void setCurrent(ALCContext context) {
		ALC.context = context;
	}

	public static ALCCapabilities getCapabilities() {
		return context.getCapabilities();
	}

	// We could remove the method below if we add support for it in the code generator.
	// But this is the only occurance of this pattern, so not really worth it.

	/**
	 * Obtains string values from ALC. This is a custom implementation for those tokens that return a list of strings instead of a single string.
	 *
	 * @param deviceHandle the device to query
	 * @param token        the information to query. One of:<p/>{@link ALC11#ALC_ALL_DEVICES_SPECIFIER}, {@link ALC11#ALC_CAPTURE_DEVICE_SPECIFIER}
	 */
	public static List<String> getStringList(long deviceHandle, int token) {
		long __result = nalcGetString(deviceHandle, token);
		if ( __result == NULL )
			return null;

		ByteBuffer buffer = memByteBuffer(__result, Integer.MAX_VALUE);

		List<String> strings = new ArrayList<String>();

		int offset = 0;
		while ( true ) {
			if ( buffer.get() == 0 ) {
				int limit = buffer.position() - 1;
				if ( limit == offset ) // Previous char was also a \0 == end of list.
					break;

				// Prepare for decoding
				buffer.position(offset); // Start index
				buffer.limit(limit); // \0 index

				// Decode
				strings.add(memDecodeUTF8(buffer));

				// Reset
				buffer.limit(Integer.MAX_VALUE);
				buffer.position(offset = limit + 1); // index after \0
			}
		}

		return strings;
	}

	/**
	 * Bootstrapping code that creates the ALCCapabilities instance.
	 *
	 * @return the ALCCapabilities instance
	 */
	static ALCCapabilities createCapabilities(long device) {
		// We don't have an ALCCapabilities instance when this method is called
		// so we have to use the native bindings directly.
		long GetIntegerv = functionProvider.getFunctionAddress("alcGetIntegerv");
		long GetString = functionProvider.getFunctionAddress("alcGetString");
		long IsExtensionPresent = functionProvider.getFunctionAddress("alcIsExtensionPresent");

		if ( GetIntegerv == NULL || GetString == NULL || IsExtensionPresent == NULL )
			throw new IllegalStateException("Core ALC functions could not be found. Make sure that OpenAL has been loaded.");

		APIBuffer __buffer = apiBuffer();

		nalcGetIntegerv(device, ALC_MAJOR_VERSION, 1, __buffer.address(), GetIntegerv);
		nalcGetIntegerv(device, ALC_MINOR_VERSION, 1, __buffer.address() + 4, GetIntegerv);

		int majorVersion = __buffer.intValue(0);
		int minorVersion = __buffer.intValue(4);

		int[][] ALC_VERSIONS = {
			{ 0, 1 },  // ALC 1
		};

		Set<String> supportedExtensions = new HashSet<String>(16);

		for ( int major = 1; major <= ALC_VERSIONS.length; major++ ) {
			int[] minors = ALC_VERSIONS[major - 1];
			for ( int minor : minors ) {
				if ( major < majorVersion || (major == majorVersion && minor <= minorVersion) )
					supportedExtensions.add("OpenALC" + Integer.toString(major) + Integer.toString(minor));
			}
		}

		// Parse EXTENSIONS string
		String extensionsString = memDecodeUTF8(memByteBufferNT1(checkPointer(nalcGetString(device, ALC_EXTENSIONS, GetString))));

		/*
		OpenALSoft: ALC_ENUMERATE_ALL_EXT ALC_ENUMERATION_EXT ALC_EXT_CAPTURE ALC_EXT_DEDICATED ALC_EXT_disconnect ALC_EXT_EFX ALC_EXT_thread_local_context
		ALC_SOFT_loopback
		Creative: ALC_ENUMERATE_ALL_EXT ALC_ENUMERATION_EXT ALC_EXT_CAPTURE ALC_EXT_EFX
		 */

		StringTokenizer tokenizer = new StringTokenizer(extensionsString);
		while ( tokenizer.hasMoreTokens() ) {
			String extName = tokenizer.nextToken();
			ByteBuffer nameBuffer = memEncodeASCII(extName);
			if ( nalcIsExtensionPresent(device, memAddress(nameBuffer), IsExtensionPresent) )
				supportedExtensions.add(extName);
		}

		return new ALCCapabilities(getFunctionProvider(), device, supportedExtensions);
	}

	static <T> T checkExtension(String extension, T functions, boolean supported) {
		if ( supported )
			return functions;
		else {
			LWJGLUtil.log("[ALC] " + extension + " was reported as available but an entry point is missing.");
			return null;
		}
	}

}