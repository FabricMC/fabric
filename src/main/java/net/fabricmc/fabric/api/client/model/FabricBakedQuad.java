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
import net.minecraft.block.Block;
import net.minecraft.client.render.block.BlockRenderLayer;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

/**
 * Mirror of {@link BakedQuad} in purpose and operation but provides extended
 * vertex data and does not force concrete subclassing of BakedQuad.<p>
 * 
 * Fabric causes BakedQuads to implement FabricBakedQuad, so you if you
 * already have a BakedQuad instance you can safely cast it to FabricBakedQuad
 * any place a FabricBakedQuad instance is required.<p>
 * 
 * Conversely, a FabricBakedQuad does <em>not</em> need to be a BakedQuad. 
 * However, it is trivial to convert to convert to a BakedQuad when vertex
 * formats are compatible and a default method for that purpose is provided.<p>
 * 
 * For compatibility, implementations must ensure the first texture layer is
 * always the desired default appearance when a rendering plugin is not available,
 * and also ensure the first layer is associated with {@link Block#getRenderLayer()}.<p>
 * 
 * @see {@link #toBakedQuad()}<p>
 */
public interface FabricBakedQuad {
    /**
     * Serves the same purpose as {@link BakedQuad#getVertexData()} but
     * data may be extended to conform to a fabric vertex format.<p>
     * 
     * The other significant difference is that quad data does not 
     * need to start at array index 0. Consumer will look for vertex
     * data starting at {@link #firstVertexIndex()}.  This can improve
     * efficiency for implementations that keep multiple quads in a single array.
     */
    int[] getVertexData();

    /**
     * Implementations that store vertex data for multiple quads in the same array
     * can override this to provide the array index for vertex data instead of copying 
     * data to a separate array. Consumers of {@link #getVertexData()} must use
     * this index if non-zero.
     */
    default int firstVertexIndex() {
        return 0;
    }

    /**
     * Same as {@link BakedQuad#hasColor()} but will not apply to texture
     * layers for which it is disabled via {@link #getLightingFlags()}, when the
     * active render plug-in supports that feature.
     */
    default boolean hasColor() {
        return getColorIndex() != -1;
    }

    /**
     * Same as {@link BakedQuad#getColorIndex()} but will not apply to texture
     * layers for which it is disabled via {@link #getLightingFlags()}, when the
     * active render plug-in supports that feature.
     */
    default int getColorIndex() {
        return -1;
    }

    /**
     * Same as {@link BakedQuad#getFace()}. Identifies the geometric face of 
     * this polygon. Must be null if not co-planar with a block face.
     */
    Direction getFace();

    /**
     * Same as {@link BakedQuad#getSprite()}  This is typically used for
     * block-breaking particles and rendering plug-ins are not required
     * to use it or implement any special handling for it.
     */
    Sprite getSprite();
     
    /**
     * Bit flags identifying the BlockRenderLayer for each layer in this quad. <p>
     * 
     * When a {@link RenderPlugin#equals(Object)} is active and this model is
     * rendered in a block context, the plug in will use this information to 
     * control alpha, cutout and mipmapping for the texture layers in this quad.<p>
     * 
     * If zero, or if no render plug in is active, this value is ignored and block models
     * will be rendered using the render layer of the block associated with this model.
     * Render plug-ins may or may not honor these flags for item models.  If not, quads
     * in item models will typically be rendered with translucency enabled.<p>
     * 
     * Format is two bits per layer, with values 0-3 corresponding to {@link BlockRenderLayer} ordinal
     * with the first texture layer in the least-significant bit position. For example, {@link BlockRenderLayer#TRANSLUCENT}
     * for layer 3 would be represented as 0b110000. Up to sixteen texture layers can be represented, 
     * though few if any rendering plug-ins are ever likely to support that many.
     * 
     * Some logical restrictions and best practices apply for quads that 
     * do not specify a custom shader...<p>
     * 
     * If SOLID occurs it should be first. Only one SOLID layer is logically useful, 
     * because any layer before SOLID would be overwritten in the frame buffer.<p>
     * 
     * TRANSLUCENT layers should be after SOLID and CUTOUT layers, though it
     * may not matter for render plug-ins using shaders for texture blending.<p>
     * 
     * Block Entity Renderer quads have an additional, important restriction:
     * If TRANSLUCENT occurs, then SOLID <em>must</em> also occur (as the first layer).
     * This is necessary because transparency sort for that scenario is typically not feasible 
     * with contemporary hardware, but translucent overlay textures on solid quads 
     * can be rendered correctly without sorting. This specification does not
     * define what a render plug-in will do with stand-alone translucent BER quads, 
     * but unless the plug-in is doing <em>very</em> sophisticated transparency handling,
     * it almost certainly will not be the desired result.<p>
     * 
     * Lastly, note that CUTOUT textures are likely to cause Z-fighting if a solid
     * texture or other CUTOUT textures are also present. Most plug-ins will not try to prevent 
     * Z-fighting by "bumping" overlay layers out (or will fail to do so) because that 
     * approach is unreliable at longer render distances due to the single-precision floating 
     * point calculations used in the GPU.<p>
     * 
     * To avoid Z-fighting with multiple texture layers, mods can either require a render plug-in
     * that correctly renders SOLID-CUTOUT overlays correctly via in-shader texture blending,
     * or use a SOLID base texture with one or more TRANSLUCENT overlay textures. The second
     * approach is reliable in almost all cases, and is unlikely to cause a performance
     * burden for plug-ins that offer in-shader texture blending, but does limit texture 
     * selection to textures designed for translucent renders.<p>
     */
    int getRenderLayerFlags();
    
    /**
     * Describes the byte-wise organization of data returned by {@link #getVertexData()}.<p>
     * 
     * If no render plug-in is active, then implementations MUST ensure this value is one of the 
     * standard Minecraft formats appropriate to the current render context (item or block).
     * Alternatively, the implementation must override {@link #toBakedQuad()} and translate the
     * vertex data to the appropriate standard format. The first approach is typically more performant.<p>
     * 
     * If a render plug-in is present, this value must be one of the standard Minecraft formats
     * (which all plug-ins will support) or a format supported by the plug in.  Rendering behavior
     * in the case of unsupported formats is not defined by this specification.
     */
    FabricVertexFormat getFormat();

    /**
     * This is a transient handle (valid only for this game session) to a shader
     * provided by the active render plug-in. There is no requirement that render
     * plug-ins support shaders, and the default value of -1 indicates that 
     * "standard" rendering should be used for this quad.<p>
     * 
     * What a "standard" render looks like is entirely at the discretion of the 
     * render plug-in author(s), so that players, pack makers and mod authors 
     * can make their own decisions based on aesthetics and/or performance considerations.<p>
     * 
     * If the active render plug-in does support shaders, quads that specify a
     * shader must return a value obtained using extensions defined and implemented
     * by the plug-in.  It is hoped the Fabric community will adopt common standards
     * for shaders and uniforms, but those are outside the scope of the Fabric API.
     */
    default int getShaderId() {
        return -1;
    }
    
    /**
     * Bitwise flags indicating color and lighting treatment requested of the 
     * active rendering plug-in.<p>
     * 
     * The available flags and their effects are documented immediately below this method.
     * To construct a value, flags are simply added or bitwise or'd together.<p>
     * 
     * The large number of options and the fact that the options are queried for
     * every quad are the rationale for a bit-wise representation with a single 
     * access method. Models are never required to implement this feature
     * and the flags are specified so that a zero value always indicates normal block 
     * tint and normal lighting.<p>
     * 
     * Render plug-ins are not required to implement all the specified options.
     * Models can query the available features via {@link RenderPlugin#supportedLightingFlags()}.
     * Set bits in that result will correspond to bit positions of the flags specified below.<p>
     */
    default int getLightingFlags() {
        return 0;
    }
    
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
    static final int RENDER_RAW_LIGHTMAP = 1;
    
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
    static final int RENDER_RAW_NORMAL = 1 << 1;
    
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
    static final int RENDER_RAW_COLORS = 1 << 2;
    
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
    static final int RENDER_RAW_EVERYTHING = 1 << 3;
    
    /**
     * When set, signal render pipeline not to apply block
     * tinting to texture colors in the first texture layer.
     * Block tint will be applied by default unless this flag is set.
     */
    static final int RENDER_DISABLE_BLOCK_TINT_0 = 1 << 4;
    
    /**
     * The same as {@link #RENDER_DISABLE_BLOCK_TINT_0} but
     * applies to the second texture layer. This has no
     * effect if the texture format does not include a 
     * second texture layer or if the active pipeline does 
     * not support the required texture depth.
     */
    static final int RENDER_DISABLE_BLOCK_TINT_1 = 1 << 5;
    
    /**
     * The same as {@link #RENDER_DISABLE_BLOCK_TINT_0} but
     * applies to the third texture layer. This has no
     * effect if the texture format does not include a 
     * third texture layer or if the active pipeline does 
     * not support the required texture depth.
     */
    static final int RENDER_DISABLE_BLOCK_TINT_2 = 1 << 6;
    
    /**
     * The same as {@link #RENDER_DISABLE_BLOCK_TINT_0} but
     * applies to the fourth texture layer. This has no
     * effect if the texture format does not include a 
     * fourth texture layer or if the active pipeline does 
     * not support the required texture depth.
     */
    static final int RENDER_DISABLE_BLOCK_TINT_3 = 1 << 7;
    
    /**
     * When set, signal render pipeline not to apply
     * the emissive lightmap to pixels in the first texture layer.
     * Non-zero emissive lightmaps will be applied by default unless this flag is set.
     * Has no effect if the texture format does not include
     * an emissive light map or if the active render pipeline
     * does not support them.
     */
    static final int RENDER_DISABLE_LIGHTMAP_0 = 1 << 8;
    
    /**
     * The same as {@link #RENDER_DISABLE_LIGHTMAP_0} but
     * applies to the second texture layer. This has no
     * effect if the texture format does not include a 
     * second texture layer or does not include an 
     * emissive light map. It also has no effect if 
     * the active render pipeline does not support emissive
     * light maps or does not support the required texture depth.
     */
    static final int RENDER_DISABLE_LIGHTMAP_1 = 1 << 9;
    
    /**
     * The same as {@link #RENDER_DISABLE_LIGHTMAP_0} but
     * applies to the third texture layer. This has no
     * effect if the texture format does not include a 
     * third texture layer or does not include an 
     * emissive light map. It also has no effect if 
     * the active render pipeline does not support emissive
     * light maps or does not support the required texture depth.
     */
    static final int RENDER_DISABLE_LIGHTMAP_2 = 1 << 10;
    
    /**
     * The same as {@link #RENDER_DISABLE_LIGHTMAP_0} but
     * applies to the fourth texture layer. This has no
     * effect if the texture format does not include a 
     * fourth texture layer or does not include an 
     * emissive light map. It also has no effect if 
     * the active render pipeline does not support emissive
     * light maps or does not support the required texture depth.
     */
    static final int RENDER_DISABLE_LIGHTMAP_3 = 1 << 11;
    
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
    static final int RENDER_DISABLE_WORLD_DIFFUSE = 1 << 12;
    
    /**
     * Similar to {@link #RENDER_DISABLE_WORLD_AO} but controls
     * the ambient occlusion component of color modification as applied
     * to world lighting. Disabled by default.
     */
    static final int RENDER_DISABLE_WORLD_AO = 1 << 13;
    
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
    static final int RENDER_ENABLE_LIGHTMAP_DIFFUSE = 1 << 14;
    
    /**
     * Similar to {@link #RENDER_DISABLE_LIGHTMAP_DIFFUSE} but controls
     * the ambient occlusion component of color modification as applied
     * to emissive lighting. Disabled by default.
     */
    static final int RENDER_ENABLE_LIGHTMAP_AO = 1 << 15;
    
    /**
     * Flags in the range of this mask are reserved for future expansion of this specification.
     */
    static final int RENDER_FABRIC_RESERVED_MASK = 0xFF << 16;
    
    /**
     * Flags in the range of this mask are reserved for use by render plug-in implementation.
     * Effects of flags in this range are at the discretion of the plug-in developers.
     */
    static final int RENDER_PIPELINE_CUSTOM_MASK = 0xFF << 24; 
    
    /**
     * Create a new baked quad based on the first texture layer.<p>
     * 
     * Using an internal vertex format that is binary-compatible with Minecraft 
     * standards (meaning the first 28 integers specify match a vanilla quad) is
     * useful as a means to ensure this is a trivial operation
     * because the need for backward compatibility is inevitable.<p>
     * 
     * Implementations using non-standard vertex formats must override this
     * method to handle conversion appropriately. Non-dynamic implementations 
     * should consider overriding this method to cache the result.<p>
     * 
     * @see #getFormat()
     */
    default BakedQuad toBakedQuad() {
        int[] vertexData = new int[28];
        System.arraycopy(getVertexData(), firstVertexIndex(), vertexData, 0, 28);
        int color0 = getColorIndex(); // TODO - check if color applies to layer 0
        return new BakedQuad(vertexData, color0, getFace(), getSprite());
    }
    
    /**
     * All implementations of FabricBakedQuad are expected to be mutable by default.
     * While this is not necessarily an accurate assumption, it is safest to assume
     * any instance is mutable unless it explicitly declares itself to be immutable.<p>
     * 
     * "Immutable" in this case means that all <em>public</em> properties and vertex data
     * reported by this instance will never change.  An immutable instance can therefore
     * be reliably wrapped or aggregated by some other mod or implementation without copying.<p>
     * 
     * Note this implies an immutable quad will have either a block or item vertex format, and
     * will have no way for this to be changed.  In particular, mod authors should be 
     * cautious of capturing a quad with an unspecified (context-dependent) vertex format
     * and using it in a different context.  In such cases, depending on usage,  it may be
     * necessary to infer the format based on the context in which the quad was obtained, 
     * and then create an instance with the specific and defined vertex format you require.<p>
     * 
     * Consumers that "wrap" or keep a reference to an FabricBakedQuad instance should always
     * check {@link #isImmutable()} and if it returns false, construct an immutable reference 
     * by copying the properties and vertex data of the mutable instance.<p>
     */
    default boolean isImmutable() {
        // TODO: this should be overridden to true for instances that are known to 
        // be immutable outputs of static model loading.  However, that will require
        // some analysis of subclasses and likely injection scenarios to provide
        // a reliable result.  Not implemented in the current version.
        // Quads should be easier than models and should be done first.
        return false;
    }
}
