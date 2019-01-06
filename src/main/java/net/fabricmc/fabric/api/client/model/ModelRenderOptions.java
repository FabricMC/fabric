/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.api.client.model;

import net.fabricmc.fabric.api.client.render.RenderPlugin;

/**
 * Defines the bit flags for {@link FabricBakedQuad#getLightingFlags()}
 * and {@link RenderPlugin#supportedLightingFlags()}.
 */
public abstract class ModelRenderOptions {
    // never meant to be instantiated
    private ModelRenderOptions() {};
    
    /**
     * When set, instructs the render plug-in to send model values for
     * lightmap without modification to the shader. For example,
     * most render plug-ins will convert a color lightmap to a
     * monochrome lightmap when emulating a standard Minecraft 
     * render and may use some of the freed bits for sideband
     * information to control the standard shaders.<p>
     * 
     * In that scenario, a custom shader that does not require
     * lighting information may use this flag to repurpose the 
     * lightmap bytes for a different purpose. The plug-in will 
     * typically present the raw values in the same vertex attributes
     * in order to avoid a vertex attribute rebind.<p>
     * 
     * This flag should have no effect for render plug-ins 
     * that do not support shaders. Also has no effect for 
     * vertex formats that do not include lightmaps (obviously).
     */
    public static final int RENDER_RAW_LIGHTMAP = 1;
    
    /**
     * Similar to {@link #RENDER_RAW_LIGHTMAP}, but applies to
     * vertex normals.  When set, instructs the render plug-in to 
     * send model values for vertex normals without modification 
     * to the shader.<p>
     * 
     * Because packed vertex normals usually only consume three bytes by 
     * themselves, any purpose served by the open byte (which varies by implementation)
     * will no longer be supported when this flag is set.  The effects of this on a standard
     * render could be drastic but may not matter for a custom shader.<p>
     * 
     * This setting is typically only useful for custom shaders that
     * do not require in-world lighting based on vertex normals 
     * and which do not depend on any accompanying control information
     * (or wish to override it.)<p>
     * 
     * This flag should have no effect for render plug-ins 
     * that do not support shaders. Also has no effect for 
     * vertex formats that do not include vertex normals (obviously).
     */
    public static final int RENDER_RAW_NORMAL = 1 << 1;
    
    /**
     * Similar to {@link #RENDER_RAW_LIGHTMAP}, but applies to
     * color values.  When set, instructs the render plug-in to 
     * send model values for color values normals without modification 
     * to the shader. All colors in the vertex format are affected.<p>
     * 
     * Modification of color values is highly dependent on the type
     * of lighting model implemented by the render plug in. The 
     * conventional Minecraft render applies all color modification
     * except for lightmap prior to vertex buffering. More modern
     * implementations might perform no color modification at all, 
     * instead opting to handle all lighting in shaders.<p>
     * 
     * Some pipelines could conceivable ignore alpha color values for solid
     * render layers, using the spare bits to send control information to their
     * standard render shaders.  This is not a recommended practice, but
     * plug-ins with this scheme should buffer unaltered alpha values when
     * this flag is set.<p>
     * 
     * This setting is typically only useful for custom shaders that
     * want to ensure color values remain entirely unmodified by the pipeline
     * and which are not not negatively affected by doing so.<p>
     * 
     * When set, all block tinting is disabled for all layers,
     * irrespective of values for the BLOCK_TINT flags below.<p>
     * 
     * This flag should have no effect for render plug-ins 
     * that do not support shaders.
     */
    public static final int RENDER_RAW_COLORS = 1 << 2;
    
    /**
     * When set, signals the render pipeline to 
     * transfer all vertex data to the vertex buffer with NO modification.<p>
     * 
     * This flag includes the effects of the three flags that come 
     * before it, and is thus partially redundant, but ensures a
     * a "raw" configuration that is absolutely comprehensive.<p>
     * 
     * Obviously, this is mostly of use to fully custom shaders.
     * Even the most extravagantly sophisticated standard shaders
     * will be incapable of interpreting arbitrary vertex data.<p>
     * 
     * Note also that render plug-ins are not required to use
     * GPU-side vertex buffers that are binary compatibile with
     * model vertex format.  Thus, "unmodified" may be subject
     * to interpretation or limitation depending on the vertex format
     * and specific plug-in implementation.<p>
     * 
     * Furthermore, when this flag is set, ALL the flags below
     * related to block tint, lightmap, shading, etc. have NO effect.<p>
     *  
     * This flag should have no effect for render plug-ins 
     * that do not support shaders.
     */
    public static final int RENDER_RAW_EVERYTHING = 1 << 3;
    
    /**
     * When set, signal render pipeline not to apply block
     * tinting to texture colors in the first texture layer.
     * Block tint will be applied by default unless this flag is set.
     */
    public static final int RENDER_DISABLE_BLOCK_TINT_0 = 1 << 4;
    
    /**
     * The same as {@link #RENDER_DISABLE_BLOCK_TINT_0} but
     * applies to the second texture layer. This has no
     * effect if the texture format does not include a 
     * second texture layer or if the active pipeline does 
     * not support the required texture depth.
     */
    public static final int RENDER_DISABLE_BLOCK_TINT_1 = 1 << 5;
    
    /**
     * The same as {@link #RENDER_DISABLE_BLOCK_TINT_0} but
     * applies to the third texture layer. This has no
     * effect if the texture format does not include a 
     * third texture layer or if the active pipeline does 
     * not support the required texture depth.
     */
    public static final int RENDER_DISABLE_BLOCK_TINT_2 = 1 << 6;
    
    /**
     * The same as {@link #RENDER_DISABLE_BLOCK_TINT_0} but
     * applies to the fourth texture layer. This has no
     * effect if the texture format does not include a 
     * fourth texture layer or if the active pipeline does 
     * not support the required texture depth.
     */
    public static final int RENDER_DISABLE_BLOCK_TINT_3 = 1 << 7;
    
    /**
     * When set, signal render pipeline not to apply
     * the emissive lightmap to pixels in the first texture layer.
     * Non-zero emissive lightmaps will be applied by default unless this flag is set.
     * Has no effect if the texture format does not include
     * an emissive light map or if the active render pipeline
     * does not support them.
     */
    public static final int RENDER_DISABLE_LIGHTMAP_0 = 1 << 8;
    
    /**
     * The same as {@link #RENDER_DISABLE_LIGHTMAP_0} but
     * applies to the second texture layer. This has no
     * effect if the texture format does not include a 
     * second texture layer or does not include an 
     * emissive light map. It also has no effect if 
     * the active render pipeline does not support emissive
     * light maps or does not support the required texture depth.
     */
    public static final int RENDER_DISABLE_LIGHTMAP_1 = 1 << 9;
    
    /**
     * The same as {@link #RENDER_DISABLE_LIGHTMAP_0} but
     * applies to the third texture layer. This has no
     * effect if the texture format does not include a 
     * third texture layer or does not include an 
     * emissive light map. It also has no effect if 
     * the active render pipeline does not support emissive
     * light maps or does not support the required texture depth.
     */
    public static final int RENDER_DISABLE_LIGHTMAP_2 = 1 << 10;
    
    /**
     * The same as {@link #RENDER_DISABLE_LIGHTMAP_0} but
     * applies to the fourth texture layer. This has no
     * effect if the texture format does not include a 
     * fourth texture layer or does not include an 
     * emissive light map. It also has no effect if 
     * the active render pipeline does not support emissive
     * light maps or does not support the required texture depth.
     */
    public static final int RENDER_DISABLE_LIGHTMAP_3 = 1 << 11;
    
    /**
     * If set, world light applied to all texture layers will not
     * modified for diffuse effects based on face/vertex normals.  
     * In the Minecraft standard lighting model diffuse shading is 
     * arbitrary (doesn't reflect movement of the sun, for example) 
     * but render plug-ins with more realistic models may shade differently.
     * 
     * Disabled by default. Rarely useful in world lighting but 
     * needed for some cutout textures (vines, for example) that are pre-shaded 
     * or which render poorly with diffuse shading.
     */
    public static final int RENDER_DISABLE_WORLD_DIFFUSE = 1 << 12;
    
    /**
     * Similar to {@link #RENDER_DISABLE_WORLD_AO} but controls
     * the ambient occlusion component of color modification as applied
     * to world lighting. Disabled by default.
     */
    public static final int RENDER_DISABLE_WORLD_AO = 1 << 13;
    
    /**
     * If set, the emissive light map WILL be modified for diffuse effects
     * based on face/vertex normals. Disabled by default.<p>
     * 
     * The effect of this flag is usually contrary to the purpose of 
     * providing an emissive light map, but can sometimes achieve subtle, partially-
     * emissive shading effects.  Such lighting hacks are effective in simple
     * lighting models but likely to give poor results in deferred rendering 
     * schemes with advanced global illumination models.<p>
     * 
     * Baked models that use this feature will want to use different techniques 
     * (to be specified by the rendering plug-in) to achieve equivalent results.<p>
     */
    public static final int RENDER_ENABLE_LIGHTMAP_DIFFUSE = 1 << 14;
    
    /**
     * Similar to {@link #RENDER_DISABLE_LIGHTMAP_DIFFUSE} but controls
     * the ambient occlusion component of color modification as applied
     * to emissive lighting. Disabled by default.
     */
    public static final int RENDER_ENABLE_LIGHTMAP_AO = 1 << 15;
    
    /**
     * Flags in the range of this mask are reserved for future expansion of this specification.
     */
    public static final int RENDER_FABRIC_RESERVED_MASK = 0xFF << 16;
    
    /**
     * Flags in the range of this mask are reserved for use by render plug-in implementation.
     * Effects of flags in this range are at the discretion of the plug-in developers.
     */
    public static final int RENDER_PIPELINE_CUSTOM_MASK = 0xFF << 24; 
}
