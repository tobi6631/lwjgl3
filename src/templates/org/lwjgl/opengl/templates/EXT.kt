/*
 * Copyright LWJGL. All rights reserved.
 * License terms: http://lwjgl.org/license.php
 */
package org.lwjgl.opengl.templates

import org.lwjgl.generator.*
import org.lwjgl.opengl.*

val EXT_abgr = "EXTABGR".nativeClassGL("EXT_abgr", postfix = EXT) {
	documentation =
		"""
		Native bindings to the $registryLink extension.

		EXT_abgr extends the list of host-memory color formats. Specifically, it provides a reverse-order alternative to image format RGBA. The ABGR component
		order matches the cpack Iris GL format on big-endian machines.
		"""

	IntConstant.block(
		"Accepted by the {@code format} parameter of DrawPixels, GetTexImage, ReadPixels, TexImage1D, and TexImage2D.",

		"ABGR_EXT" _ 0x8000
	)
}

val EXT_bgra = "EXTBGRA".nativeClassGL("EXT_bgra", postfix = EXT) {
	documentation =
		"""
		Native bindings to the $registryLink extension.

		EXT_bgra extends the list of host-memory color formats. Specifically, it provides formats which match the memory layout of Windows DIBs so that
		applications can use the same data in both Windows API calls and OpenGL pixel API calls.

		${GL12.promoted}
		"""

	IntConstant.block(
		"Accepted by the {@code format} parameter of DrawPixels, GetTexImage, ReadPixels, TexImage1D, and TexImage2D.",

		"BGR_EXT" _ 0x80E0,
		"BGRA_EXT" _ 0x80E1
	)
}

val EXT_bindable_uniform = "EXTBindableUniform".nativeClassGL("EXT_bindable_uniform", postfix = EXT) {
	nativeImport (
		"OpenGL.h"
	)

	documentation =
		"""
		Native bindings to the $registryLink extension.

		This extension introduces the concept of bindable uniforms to the OpenGL Shading Language. A uniform variable can be declared bindable, which means that
		the storage for the uniform is not allocated by the compiler/linker anymore, but is backed by a buffer object. This buffer object is bound to the
		bindable uniform through the new command UniformBufferEXT(). Binding needs to happen after linking a program object.

		Binding different buffer objects to a bindable uniform allows an application to easily use different "uniform data sets", without having to re-specify
		the data every time.

		A buffer object can be bound to bindable uniforms in different program objects. If those bindable uniforms are all of the same type, accessing a
		bindable uniform in program object A will result in the same data if the same access is made in program object B. This provides a mechanism for
		'environment uniforms', uniform values that can be shared among multiple program objects.
		"""

	IntConstant.block(
		"Accepted by the {@code pname} parameter of GetBooleanv, GetIntegerv, GetFloatv, and GetDoublev.",

		"MAX_VERTEX_BINDABLE_UNIFORMS_EXT" _ 0x8DE2,
		"MAX_FRAGMENT_BINDABLE_UNIFORMS_EXT" _ 0x8DE3,
		"MAX_GEOMETRY_BINDABLE_UNIFORMS_EXT" _ 0x8DE4,
		"MAX_BINDABLE_UNIFORM_SIZE_EXT" _ 0x8DED,
		"UNIFORM_BUFFER_BINDING_EXT" _ 0x8DEF
	)

	IntConstant.block(
		"Accepted by the {@code target} parameters of BindBuffer, BufferData, BufferSubData, MapBuffer, UnmapBuffer, GetBufferSubData, and GetBufferPointerv.",

		"UNIFORM_BUFFER_EXT" _ 0x8DEE
	)

	GLvoid.func(
		"UniformBufferEXT",
		"",

		GLuint.IN("program", ""),
		GLint.IN("location", ""),
		GLuint.IN("buffer", "")
	)

	GLint.func(
		"GetUniformBufferSizeEXT",
		"",

		GLuint.IN("program", ""),
		GLint.IN("location", "")
	)

	GLintptr.func(
		"GetUniformOffsetEXT",
		"",

		GLuint.IN("program", ""),
		GLint.IN("location", "")
	)
}

val EXT_blend_color = "EXTBlendColor".nativeClassGL("EXT_blend_color", postfix = EXT) {
	nativeImport (
		"OpenGL.h"
	)

	documentation =
		"""
		Native bindings to the $registryLink extension.

		Blending capability is extended by defining a constant color that can be included in blending equations. A typical usage is blending two RGB images.
		Without the constant blend factor, one image must have an alpha channel with each pixel set to the desired blend factor.

		${GL14.promoted}
		"""

	IntConstant.block(
		"Accepted by the {@code sfactor} and {@code dfactor} parameters of BlendFunc.",

		"CONSTANT_COLOR_EXT" _ 0x8001,
		"ONE_MINUS_CONSTANT_COLOR_EXT" _ 0x8002,
		"CONSTANT_ALPHA_EXT" _ 0x8003,
		"ONE_MINUS_CONSTANT_ALPHA_EXT" _ 0x8004
	)

	IntConstant.block(
		"Accepted by the {@code pname} parameter of GetBooleanv, GetIntegerv, GetFloatv, and GetDoublev.",

		"BLEND_COLOR_EXT" _ 0x8005
	)

	GLvoid.func(
		"BlendColorEXT",
		"",

		GLclampf.IN("red", ""),
		GLclampf.IN("green", ""),
		GLclampf.IN("blue", ""),
		GLclampf.IN("alpha", "")
	)
}

val EXT_blend_equation_separate = "EXTBlendEquationSeparate".nativeClassGL("EXT_blend_equation_separate", postfix = EXT) {
	nativeImport (
		"OpenGL.h"
	)

	documentation =
		"""
		Native bindings to the $registryLink extension.

		${registryLinkTo("EXT", "blend_func_separate")} introduced separate RGB and alpha blend factors. ${registryLinkTo("EXT", "blend_minmax")} introduced a
		distinct blend equation for combining source and destination blend terms. (${registryLinkTo("EXT_blend_subtract", "blend_subtract")} &
		${registryLinkTo("EXT", "blend_logic_op")} added other blend equation modes.) OpenGL 1.4 integrated both functionalities into the core standard.

		While there are separate blend functions for the RGB and alpha blend factors, OpenGL 1.4 provides a single blend equation that applies to both RGB and
		alpha portions of blending.

		This extension provides a separate blend equation for RGB and alpha to match the generality available for blend factors.

		Requires ${GL14.core} or ${ARB_imaging.link} or ${registryLinkTo("EXT", "blend_minmax")} and/or
		${registryLinkTo("EXT_blend_subtract", "blend_subtract")}. ${GL20.promoted}
		"""

	IntConstant.block(
		"Accepted by the {@code pname} parameter of GetBooleanv, GetIntegerv, GetFloatv, and GetDoublev.",

		"BLEND_EQUATION_RGB_EXT" _ 0x8009,
		"BLEND_EQUATION_ALPHA_EXT" _ 0x883D
	)

	GLvoid.func(
		"BlendEquationSeparateEXT",
		"",

		GLenum.IN("modeRGB", ""),
		GLenum.IN("modeAlpha", "")
	)
}

val EXT_blend_func_separate = "EXTBlendFuncSeparate".nativeClassGL("EXT_blend_func_separate", postfix = EXT) {
	nativeImport (
		"OpenGL.h"
	)

	documentation =
		"""
		Native bindings to the $registryLink extension.

		Blending capability is extended by defining a function that allows independent setting of the RGB and alpha blend factors for blend operations that
		require source and destination blend factors. It is not always desired that the blending used for RGB is also applied to alpha.

		${GL14.promoted}
		"""

	IntConstant.block(
		"Accepted by the {@code pname} parameter of GetBooleanv, GetIntegerv, GetFloatv, and GetDoublev.",

		"BLEND_DST_RGB_EXT" _ 0x80C8,
		"BLEND_SRC_RGB_EXT" _ 0x80C9,
		"BLEND_DST_ALPHA_EXT" _ 0x80CA,
		"BLEND_SRC_ALPHA_EXT" _ 0x80CB
	)

	GLvoid.func(
		"BlendFuncSeparateEXT",
		"",

		GLenum.IN("sfactorRGB", ""),
		GLenum.IN("dfactorRGB", ""),
		GLenum.IN("sfactorAlpha", ""),
		GLenum.IN("dfactorAlpha", "")
	)
}

val EXT_blend_minmax = "EXTBlendMinmax".nativeClassGL("EXT_blend_minmax", postfix = EXT) {
	nativeImport (
		"OpenGL.h"
	)

	documentation =
		"""
		Native bindings to the $registryLink extension.

		Blending capability is extended by respecifying the entire blend equation. While this document defines only two new equations, the #BlendEquationEXT()
		procedure that it defines will be used by subsequent extensions to define additional blending equations.

		The two new equations defined by this extension produce the minimum (or maximum) color components of the source and destination colors. Taking the
		maximum is useful for applications such as maximum projection in medical imaging.

		${GL14.promoted}
		"""

	IntConstant.block(
		"Accepted by the {@code mode} parameter of BlendEquationEXT.",

		"FUNC_ADD_EXT" _ 0x8006,
		"MIN_EXT" _ 0x8007,
		"MAX_EXT" _ 0x8008
	)

	IntConstant.block(
		"Accepted by the {@code pname} parameter of GetBooleanv, GetIntegerv, GetFloatv, and GetDoublev.",

		"BLEND_EQUATION_EXT" _ 0x8009
	)

	GLvoid.func(
		"BlendEquationEXT",
		"",

		GLenum.IN("mode", "")
	)
}

val EXT_blend_subtract = "EXTBlendSubtract".nativeClassGL("EXT_blend_subtract", postfix = EXT) {
	documentation =
		"""
		Native bindings to the $registryLink extension.

		Two additional blending equations are specified using the interface defined by ${registryLinkTo("EXT", "blend_minmax.link")}. These equations are similar to the default
		blending equation, but produce the difference of its left and right hand sides, rather than the sum.  Image differences are useful in many image
		processing applications.

		${GL14.promoted}
		"""

	IntConstant.block(
		"Accepted by the {@code mode} parameter of BlendEquationEXT.",

		"FUNC_SUBTRACT_EXT" _ 0x800A,
		"FUNC_REVERSE_SUBTRACT_EXT" _ 0x800B
	)
}

val EXT_depth_bounds_test = "EXTDepthBoundsTest".nativeClassGL("EXT_depth_bounds_test", postfix = EXT) {
	nativeImport (
		"OpenGL.h"
	)

	documentation =
		"""
		Native bindings to the $registryLink extension.

		This extension adds a new per-fragment test that is, logically, after the scissor test and before the alpha test. The depth bounds test compares the
		depth value stored at the location given by the incoming fragment's (xw,yw) coordinates to a user-defined minimum and maximum depth value. If the stored
		depth value is outside the user-defined range (exclusive), the incoming fragment is discarded.

		Unlike the depth test, the depth bounds test has NO dependency on the fragment's window-space depth value.

		This functionality is useful in the context of attenuated stenciled shadow volume rendering. To motivate the functionality's utility in this context, we
		first describe how conventional scissor testing can be used to optimize shadow volume rendering.

		If an attenuated light source's illumination can be bounded to a rectangle in XY window-space, the conventional scissor test can be used to discard
		shadow volume fragments that are guaranteed to be outside the light source's window-space XY rectangle. The stencil increments and decrements that would
		otherwise be generated by these scissored fragments are inconsequential because the light source's illumination can pre-determined to be fully
		attenuated outside the scissored region. In other words, the scissor test can be used to discard shadow volume fragments rendered outside the scissor,
		thereby improving performance, without affecting the ultimate illumination of these pixels with respect to the attenuated light source.

		This scissoring optimization can be used both when rendering the stenciled shadow volumes to update stencil (incrementing and decrementing the stencil
		buffer) AND when adding the illumination contribution of attenuated light source's.

		In a similar fashion, we can compute the attenuated light source's window-space Z bounds (zmin,zmax) of consequential illumination. Unless a depth value
		(in the depth buffer) at a pixel is within the range [zmin,zmax], the light source's illumination can be pre-determined to be inconsequential for the
		pixel. Said another way, the pixel being illuminated is either far enough in front of or behind the attenuated light source so that the light source's
		illumination for the pixel is fully attenuated. The depth bounds test can perform this test.
		"""

	IntConstant.block(
		"""
		Accepted by the {@code cap} parameter of Enable, Disable, and IsEnabled, and by the {@code pname} parameter of GetBooleanv, GetIntegerv, GetFloatv, and
		GetDoublev.
		""",

		"DEPTH_BOUNDS_TEST_EXT" _ 0x8890
	)

	IntConstant.block(
		"Accepted by the {@code pname} parameter of GetBooleanv, GetIntegerv, GetFloatv, and GetDoublev.",

		"DEPTH_BOUNDS_EXT" _ 0x8891
	)

	GLvoid.func(
		"DepthBoundsEXT",
		"",

		GLclampd.IN("zmin", ""),
		GLclampd.IN("zmax", "")
	)
}

val EXT_stencil_wrap = "EXTStencilWrap".nativeClassGL("EXT_stencil_wrap", postfix = EXT) {
	documentation =
		"""
		Native bindings to the $registryLink extension.

		Various algorithms use the stencil buffer to "count" the number of surfaces that a ray passes through. As the ray passes into an object, the stencil
		buffer is incremented. As the ray passes out of an object, the stencil buffer is decremented.

		GL requires that the stencil increment operation clamps to its maximum value. For algorithms that depend on the difference between the sum of the
		increments and the sum of the decrements, clamping causes an erroneous result.

		This extension provides an enable for both maximum and minimum wrapping of stencil values. Instead, the stencil value wraps in both directions.

		Two additional stencil operations are specified. These new operations are similiar to the existing INCR and DECR operations, but they wrap their result
		instead of saturating it. This functionality matches the new stencil operations introduced by DirectX 6.

		${GL14.promoted}
		"""

	IntConstant.block(
		"Accepted by the {@code sfail}, {@code dpfail}, and {@code dppass} parameter of StencilOp.",

		"INCR_WRAP_EXT" _ 0x8507,
		"DECR_WRAP_EXT" _ 0x8508
	)
}

val EXT_texture_compression_s3tc = "EXTTextureCompressionS3TC".nativeClassGL("EXT_texture_compression_s3tc", postfix = EXT) {
	documentation =
		"""
		Native bindings to the $registryLink extension.

		This extension provides additional texture compression functionality specific to S3's S3TC format (called DXTC in Microsoft's DirectX API), subject to
		all the requirements and limitations described by the extension ${ARB_texture_compression.link}.

		This extension supports DXT1, DXT3, and DXT5 texture compression formats. For the DXT1 image format, this specification supports an RGB-only mode and a
		special RGBA mode with single-bit "transparent" alpha.
		"""

	IntConstant.block(
		"""
		Accepted by the {@code internalformat} parameter of TexImage2D, CopyTexImage2D, and CompressedTexImage2D and the {@code format} parameter of
		CompressedTexSubImage2D.
		""",

		"COMPRESSED_RGB_S3TC_DXT1_EXT" _ 0x83F0,
		"COMPRESSED_RGBA_S3TC_DXT1_EXT" _ 0x83F1,
		"COMPRESSED_RGBA_S3TC_DXT3_EXT" _ 0x83F2,
		"COMPRESSED_RGBA_S3TC_DXT5_EXT" _ 0x83F3
	)
}

val EXT_texture_filter_anisotropic = "EXTTextureFilterAnisotropic".nativeClassGL("EXT_texture_filter_anisotropic", postfix = EXT) {
	documentation =
		"""
		Native bindings to the $registryLink extension.

		Texture mapping using OpenGL's existing mipmap texture filtering modes assumes that the projection of the pixel filter footprint into texture space is a
		square (ie, isotropic). In practice however, the footprint may be long and narrow (ie, anisotropic). Consequently, mipmap filtering severely blurs
		images on surfaces angled obliquely away from the viewer.

		Several approaches exist for improving texture sampling by accounting for the anisotropic nature of the pixel filter footprint into texture space. This
		extension provides a general mechanism for supporting anisotropic texturing filtering schemes without specifying a particular formulation of anisotropic
		filtering.

		The extension permits the OpenGL application to specify on a per-texture object basis the maximum degree of anisotropy to account for in texture
		filtering.

		Increasing a texture object's maximum degree of anisotropy may improve texture filtering but may also significantly reduce the implementation's texture
		filtering rate. Implementations are free to clamp the specified degree of anisotropy to the implementation's maximum supported degree of anisotropy.

		A texture's maximum degree of anisotropy is specified independent from the texture's minification and magnification filter (as opposed to being
		supported as an entirely new filtering mode). Implementations are free to use the specified minification and magnification filter to select a particular
		anisotropic texture filtering scheme. For example, a NEAREST filter with a maximum degree of anisotropy of two could be treated as a 2-tap filter that
		accounts for the direction of anisotropy. Implementations are also permitted to ignore the minification or magnification filter and implement the
		highest quality of anisotropic filtering possible.

		Applications seeking the highest quality anisotropic filtering available are advised to request a GL11#LINEAR_MIPMAP_LINEAR minification filter, a
		GL11#LINEAR magnification filter, and a large maximum degree of anisotropy.
		"""

	IntConstant.block(
		"Accepted by the {@code pname} parameters of GetTexParameterfv, GetTexParameteriv, TexParameterf, TexParameterfv, TexParameteri, and TexParameteriv.",

		"TEXTURE_MAX_ANISOTROPY_EXT" _ 0x84FE
	)

	IntConstant.block(
		"Accepted by the {@code pname} parameters of GetBooleanv, GetDoublev, GetFloatv, and GetIntegerv.",

		"MAX_TEXTURE_MAX_ANISOTROPY_EXT" _ 0x84FF
	)
}