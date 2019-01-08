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

import net.fabricmc.fabric.api.client.render.FabricVertexFormat;
import net.fabricmc.fabric.api.client.render.RenderPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

/**
 * Mirror of {@link BakedQuad} in purpose and operation but supports extended
 * vertex formats and does not force concrete subclassing of BakedQuad.<p>
 * 
 * Fabric causes BakedQuads to implement FabricBakedQuad, so you if you
 * already have a BakedQuad instance you can safely cast it to FabricBakedQuad
 * any place a FabricBakedQuad instance is required.<p>
 * 
 * Conversely, a FabricBakedQuad does <em>not</em> need to be a BakedQuad. 
 * However, it is trivial to convert a FabricBakedQuad to a BakedQuad when vertex
 * formats are compatible and a default method for that purpose is provided.<p>
 * 
 * For compatibility, quads with multiple texture layers must ensure the first texture layer is
 * always the desired default appearance when a rendering plug-in is not available,
 * and also ensure the first texture layer is associated with {@link Block#getRenderLayer()}.<p>
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
     * layers for which it is disabled via {@link #getFeatureFlags()}, when the
     * active render plug-in supports that feature.
     */
    default boolean hasColor() {
        return getColorIndex() != -1;
    }

    /**
     * Same as {@link BakedQuad#getColorIndex()} but will not apply to texture
     * layers for which it is disabled via {@link #getFeatureFlags()}, when the
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

    Block b();
    
    /**
     * Bit flags identifying the BlockRenderLayer for each layer in this quad.<p>
     * 
     * When a {@link RenderPlugin} is active and this model is
     * rendered in a block context, the-plug in will use this information to 
     * control alpha, cutout and mip-mapping for the texture layers in this quad.<p>
     * 
     * If the value is negative, or if no render plug in is active, this value is ignored and block models
     * will be rendered using {@link Block#getRenderLayer()} on the block associated with this model.
     * Render plug-ins may or may not honor these flags for item models.  If not, quads
     * in item models will typically be rendered with translucency enabled.<p>
     * 
     * Format is two bits per layer, with values 0-3 corresponding to {@link BlockRenderLayer} ordinal
     * with the first texture layer in the least-significant bit position. For example, {@link BlockRenderLayer#TRANSLUCENT}
     * for layer 3 would be represented as 0b110000. Up to fifteen texture layers can be represented, 
     * though no rendering plug-ins are ever likely to support that many.<p>
     * 
     * For quads with multiple texture layers, some logical restrictions and best practices 
     * apply (absent a custom shader):<p>
     * 
     * If SOLID occurs it should appear only once and it should be for the first texture layer. 
     * This is a logical constraint - any layer before SOLID would be overwritten in the frame buffer.<p>
     * 
     * TRANSLUCENT layers should be after SOLID and CUTOUT layers, though it
     * may not matter for render plug-ins using shaders for texture blending.<p>
     * 
     * {@link FastRenderableBlockEntity} quads have an additional, important restriction:
     * If TRANSLUCENT occurs, then SOLID <em>must</em> also occur (as the first layer).
     * This is necessary because transparency sort for that scenario is typically not feasible 
     * with contemporary hardware, but translucent overlay textures on solid quads 
     * can be rendered correctly without sorting. This specification does not
     * define what a render plug-in will do with stand-alone translucent FRBE quads, 
     * but unless the plug-in is doing very sophisticated transparency handling,
     * it almost certainly will not be the desired result.<p>
     * 
     * Lastly, note that CUTOUT textures are likely to cause Z-fighting if a solid
     * texture or other CUTOUT textures are also present. Plug-ins using the fixed OpenGL 
     * pipeline may try to prevent Z-fighting by "bumping" overlay vertices a small distance
     * away from block center but that approach is unreliable at longer render distances due 
     * to the single-precision floating point calculations used in the GPU.<p>
     * 
     * To avoid Z-fighting with multiple texture layers, mods can either require a render plug-in
     * that correctly renders SOLID-CUTOUT overlays correctly via in-shader texture blending,
     * or use a SOLID base texture with one or more TRANSLUCENT overlay textures. The second
     * approach is reliable in almost all cases, and is unlikely to cause a performance
     * burden for plug-ins that offer in-shader texture blending, but does limit texture 
     * selection to textures designed for translucent renders.<p>
     */
    default int getRenderLayerFlags() {
        return -1;
    }

    /**
     * Describes the byte-wise organization of data returned by {@link #getVertexData()}.<p>
     * 
     * If no render plug-in is active, then implementations MUST ensure this value is one of the 
     * standard Minecraft formats appropriate to the current render context (item or block).
     * Alternatively, the implementation must override {@link #toBakedQuad()} and translate the
     * vertex data to the appropriate standard format. The first approach is typically more performant.<p>
     * 
     * If a render plug-in is present, this value must be one of the standard formats in {@link FabricVertexFormat}
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
     * What a standard render looks like is entirely at the discretion of the 
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
     * Bitwise flags indicating activation of special color, lighting or other 
     * optional features supported by the active rendering plug-in.<p>
     * 
     * The available flags and their effects are defined per implementation.
     * Plug-ins can adopt shared standards for feature sets. The feature set
     * for the current rendering plug-in is identified by {@link RenderPlugin#featureSetId()}.
     * To construct a value, bit flags defined by the feature set are added or bitwise or'd together.<p>
     * 
     * The large number of potential options and the fact that options are queried for
     * every quad are the rationale for a bit-wise representation with a single 
     * access method. Plug-ins are never required to implement extended options
     * and flags are always specified so that a zero value always indicates normal block 
     * tint and normal lighting.<p>
     * 
     * Render plug-ins are not required to implement all the options in the
     * feature set they adopt. Models can query the available features via {@link RenderPlugin#supportedFeatureFlags()}.
     * Set bits in that result will correspond to bit positions of the flags in the
     * feature set.<p>
     */
    default int getFeatureFlags() {
        return 0;
    }

    /**
     * Create a new baked quad based on the first texture layer.<p>
     * 
     * Using an internal vertex format that is binary-compatible with Minecraft 
     * standards (meaning the first 28 integers specify a vanilla quad) is
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
        return new BakedQuad(vertexData, getColorIndex(), getFace(), getSprite());
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
     * Note this implies an immutable quad will have fixed vertex format, with
     * no way for this to be changed.  In particular, mod authors should be 
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
