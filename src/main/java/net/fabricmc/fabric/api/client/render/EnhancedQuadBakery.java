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

package net.fabricmc.fabric.api.client.render;

import net.minecraft.client.render.block.BlockRenderLayer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

/**
 * Generates vertex data needed to implement EnhancedBakedQuad.<p>
 * 
 * "Baking" is mostly about assigning renderable texture coordinates.
 * We also assign missing vertex normals. At this point we do not 
 * know where the model will be rendered, so we cannot complete 
 * the lighting calculation but we can analyze geometry and capture
 * information that can be reused when the qauds are lit later on.<p>
 * 
 * For conventional quads, render results will be identical to normal Minecraft. However,
 * Enhanced Quads support the following additional features:<p>
 * 
 * <li>Multiple texture layers. Fabric will render these as separate quads using the
 * conventional Minecraft render pipeline but advanced rendering mods may render them in a single pass.</li><p>
 * 
 * <li>Custom lightmap / emissive rendering. You can specify an additive lightmap for emissive effects.</li><p>
 * 
 * <li>Per-layer control of BlockRenderLayer, emissive lightmap, diffuse shading and ambient occlusion.</li><p>
 * 
 * <li>Vertex normals.  These are not sent to the BlockRender pipeline (absent a rendering add-on) but are used
 * to improve diffuse shading for models with non-cube-aligned faces.</li><p>
 * 
 * <li>Support for non-square quads.  Uses an improved AO shading algorithm to handle these correctly.</li><p>
 *
 * <li>Vertex data includes face and tint usage metadata to simplify EnhancedQuad implementations and
 * enable performant and consistent application of vertex data.</li><p>
 * 
 * Usage:<br>
 * <li>Obtain an instance. You can and should reuse instances of this class if you are baking multiple quads.</li>
 * <li>Set vertex and quad attributes, texture depth, render layers, etc.  This can be done in any order.</li>
 * <li>Call {@link #outputBakedQuadData(int[], int)}</li>
 * <li>Repeat as needed with changes, optionally calling {@link #clear()} first.</li><p>
 * 
 * The underlying static implementation can also be used directly and this approach is strongly
 * recommended for Block Entity Renderers and/or extensive dynamic model baking at render time
 * in order to minimize overhead and garbage collection stutter. 
 */
public final class EnhancedQuadBakery {

    private final int[] vertexData = new int[MAX_QUAD_STRIDE];
    private final Sprite[] sprites = new Sprite[3];

    public EnhancedQuadBakery() {
	clear();
    }

    /**
     * Controls alpha and mip-map settings for each texture layer via BlockRenderLayer.<p>
     * 
     * Some restrictions apply:<p>
     * <li>If SOLID occurs it must be first. Only one SOLID layer is possible. (Anything else would be pointless.)</li>
     * <li>TRANSLUCENT layers must be "above" SOLID and CUTOUT layers.</li>
     */
    public final void setRenderLayer(TextureDepth textureLayer, BlockRenderLayer renderLayer) {
	Quad.setRenderLayer(textureLayer, renderLayer, vertexData, 0);
    }

    /**
     * Sets texture depth for next output quad. Changing does not clear values 
     * for unused layers - higher layers not used simply won't be included in output.<p>
     * 
     * SINGLE by default. Change applies to the next output quad and remains 
     * in effect for all subsequent quads until changed.
     */
    public final void setTextureDepth(TextureDepth textureDepth) {
	Quad.setTextureDepth(textureDepth, vertexData, 0);
    }

    /**
     * Sets position of vertex.  Position coordinates are always required, and for block models
     * is a value 0 to 1 relative to 0,0,0 origin.  Winding order of positions determines facing. 
     * Looking at the front of your quad, positions 0-3 should go counterclockwise.
     */
    public final void position(int vertexIndex, float x, float y, float z) {
	Vertex.position(vertexIndex, x, y, z, vertexData, 0);
    }

    /**
     * Sets a vertex normal at the given vertex index. Most models don't have
     * or need vertex normals.  If omitted, computed face normal will be used.
     */
    public final void normal(int vertexIndex, float x, float y, float z) {
	Vertex.normal(vertexIndex, x, y, z, vertexData, 0);
    }

    /**
     * Removes all previously set vertex normals without changing anything else.
     * This is the default setting so call is unnecessary otherwise.
     * Ensures computed face normal will be used for lighting.
     */
    public final void clearNormals() {
	Vertex.clearNormals(vertexData, 0);
    }

    /**
     * Assigns given texture atlas sprite to texture layer. 
     * Textures outside the texture atlas are unsupported. 
     */
    public final void setSprite(TextureDepth textureDepth, Sprite sprite) {
	sprites[textureDepth.ordinal()] = sprite;
    }

    /**
     * Assigns UV coordinates for the given vertex. UV coordinates should be in the 0-16
     * range per Minecraft convention. (Coordinates are relative to the sprite, not the texture atlas.  
     * 0,0 is origin corner, 16,16 is opposite corner. Vanilla block texture are 16x16 pixels.)<p>
     * 
      {@link #outputBakedQuadData(int[], int)} will translate these to texture atlas
      coordinates needed for rendering.
     */
    public final void uv(int vertexIndex, TextureDepth textureDepth, float u, float v) {
	Vertex.uv(vertexIndex, textureDepth, u, v, vertexData, 0);
    }

    /**
     * Assigns color to vertex for the given texture layer.  Texture color will be multiplied
     * by this color.  The alpha component is only used for TRANSLUCENT layer rendering.<p>
     * 
     * Colors low byte should be red, followed by green and blue, and alpha as the high byte. 
     * So for example, pure red opaque green is 0xFF00FF00;<p>
     * 
     * Default value for all layers is white. (0xFFFFFFFF)
     */
    public final void color(int vertexIndex, TextureDepth textureDepth, int color) {
	Vertex.color(vertexIndex, textureDepth, color, vertexData, 0);
    }

    /**
     * Version of {@link #color(int, TextureDepth, int)} that accepts unpacked int components.
     */
    public final void color(int vertexIndex, TextureDepth textureDepth,int red, int green, int blue, int alpha) {
	color(vertexIndex, textureDepth, red | (green << 8) | (blue << 16) | (alpha << 24));
    }

    /**
     * Version of {@link #color(int, TextureDepth, int)} that accepts unpacked float components.
     */
    public final void color(int vertexIndex, TextureDepth textureDepth, float red, float green, float blue, float alpha) {
	color(vertexIndex, textureDepth, Math.round(red * 255), Math.round(green * 255), Math.round(blue * 255), Math.round(alpha * 255));
    }

    /**
     * Set true if your model/quad should honor color tinting based on Biome, etc. Applied
     * per layer.  Default is true for all layers. Turning off is useful when an overlay
     * texture on top of a "natural" texture should not be tinted like the rest of the block.<p> 
     * 
     * New value will apply to the next completed quad and remain in effect for all subsequent quads until changed.
     */
    public final void enableBlockColor(TextureDepth textureLayer, boolean enable) {
	Quad.setBlockColorEnabled(textureLayer, enable, vertexData, 0);
    }

    /**
     * Use this to control lighting of the texture in the given layer.
     * If enabled, the vertex light set by {@link #setVertexLight(int)} will be added
     * to world light for this texture layer. This result in full emissive rendering if 
     * vertex light is set to white (the default.)  However, more subtle lighting effects
     * are possible with other lightmap colors.<p>
     * 
     * New value will apply to the next completed quad and remain in effect for all subsequent quads until changed.
     */
    public final void enableEmissiveLightmap(TextureDepth textureLayer, boolean enable) {
	Quad.setLightmapEnabled(textureLayer, enable, vertexData, 0);
    }

    /**
     * If enabled, textures lit by world light will have diffuse shading applied 
     * based on face/vertex normals.  In the standard lighting model shading is 
     * arbitrary (doesn't reflect movement of the sun, for example) but enhanced lighting
     * models may shade differently.
     * 
     * Enabled by default. Rarely disabled in world lighting but may be useful for some cutout
     * textures (vines, for example) that are pre-shaded or which render poorly with shading.<p>
     * 
     * New value will apply to the next completed quad and remain in effect for all subsequent quads until changed.
     */
    public final void enableWorldLightDiffuse(boolean enable) {
	Quad.setWorldDiffuseEnabled(enable, vertexData, 0);
    }

    /**
     * Works the same as {@link #enableWorldLightDiffuse(boolean)}, but applies
     * to surfaces lit by the provided vertex light map. (See {@link #enableEmissiveLightmap(TextureDepth, boolean)}.)<p>
     * 
     * Disabled by default.  Most textures with vertex lighting will be fully emissive
     * and it will not make sense to shade them. But this could be useful for partially 
     * illuminated surfaces. <p>
     * 
     * New value will apply to the next completed quad and remain in effect for all subsequent quads until changed.
     */
    public final void enableEmissiveLightmapDiffuse(boolean enable) {
	Quad.setLightmapDiffuseEnabled(enable, vertexData, 0);
    }

    /**
     * If enabled, textures lit by world light will have ambient occlusion applied. 
     * 
     * Enabled by default and changes are rarely needed in world lighting.<p>
     * 
     * New value will apply to the next completed quad and remain in effect for all subsequent quads until changed.
     */
    public final void enableWorldLightAmbientOcclusion(boolean enable) {
	Quad.setWorldAoEnabled(enable, vertexData, 0);
    }

    /**
     * Works the same as {@link #enableWorldLightAmbientOcclusion(boolean)}, but applies
     * to surfaces lit by the provided vertex light color. (See {@link #enableEmissiveLightmap(TextureDepth, boolean)}.)<p>
     * 
     * Disabled by default.  Most textures with vertex lighting will be fully emissive
     * and it will not make sense to shade them. But this could be useful for partially 
     * illuminated surfaces. <p>
     * 
     * New value will apply to the next completed quad and remain in effect for all subsequent quads until changed.
     */
    public final void enableEmissiveLightmapAmbientOcclusion(boolean enable) {
	Quad.setLightmapAoEnabled(enable, vertexData, 0);
    }

    /**
     * Accepts a light value in the form of an RGB color value to be used as the minimum block light
     * for the next <em>vertex</em>. The light will always be additive with other light sources.
     * Vertex lighting will be interpolated like any other vertex value.<p>
     * 
     * In the standard lighting model, this is converted into a monochromatic value based on luminance.
     * 
     * Vertex light is always <em>additive</em>.  For example, if you provide a dim blue vertex light and
     * your surface is next to a torch, your surface will render with mostly torch light (with flicker) but with a
     * boosted blue component. (Clamped to full white brightness.)  In direct sunlight dim vertex light probably
     * won't be noticeable.<p>
     * 
     * Will be full brightness (0xFFFFFF) by default.<br>
     * Changes apply to all subsequent vertices until changed.<p>
     */
    public final void setEmissiveLightMap(int lightRGB) {
	Quad.setEmissiveLightMap(lightRGB, vertexData, 0);
    }

    /**
     * Version of {@link #setEmissiveLightMap(int)} that accepts unpacked int components.
     */
    public final void setEmissiveLightMap(int red, int green, int blue) {
	setEmissiveLightMap(red | (green << 8) | (blue << 16));
    }

    /**
     * Version of {@link #setEmissiveLightMap(int)} that accepts unpacked float components.
     */
    public final void setEmissiveLightMap(float red, float green, float blue) {
	setEmissiveLightMap(Math.round(red * 255), Math.round(green * 255), Math.round(blue * 255));
    }

    /**
     * Returns all settings to default values.
     */
    public final void clear() {
	sprites[0] = null;
	sprites[1] = null;
	sprites[2] = null;
	vertexData[0] = Quad.DEFAULT_CONTROL_BITS;
	setEmissiveLightMap(0xFFFFFF);
	clearNormals();
    }

    /**
     * Returns the minimum array size to store this quad. Useful 
     * for {@link #outputBakedQuadData(int[], int)}
     */
    public final int quadSize() {
	return Quad.quadSize(vertexData, 0);
    }

    /**
     * Generates baked quad data in the provided array starting at the given index.<p>
     * 
     * Returns the number of integers written to the array for benefit of implementations
     * that are packing data for multiple quads into a single array.<p>
     * 
     * Will throw an exception if the array is too small. Use {@link #intSize()} to check.
     */
    public final int outputBakedQuadData(int [] target, int start) {

	// Will need to do the stuff below, with modifications and additions for the expanded feature set
	return 0;
	//    
	//      public BakedQuad bake(Vector3f vector3f_1, Vector3f vector3f_2, ModelElementFace modelElementFace_1, Sprite sprite_1, Direction direction_1, ModelRotationContainer modelRotationContainer_1, @Nullable net.minecraft.client.render.model.json.ModelRotation modelRotation_1, boolean boolean_1) {
	//          ModelElementTexture modelElementTexture_1 = modelElementFace_1.textureData;
	//          if (modelRotationContainer_1.isUvLocked()) {
	//             modelElementTexture_1 = this.method_3454(modelElementFace_1.textureData, direction_1, modelRotationContainer_1.getRotation());
	//          }
	//
	//          float[] floats_1 = new float[modelElementTexture_1.uvs.length];
	//          System.arraycopy(modelElementTexture_1.uvs, 0, floats_1, 0, floats_1.length);
	//          float float_1 = (float)sprite_1.getWidth() / (sprite_1.getMaxU() - sprite_1.getMinU());
	//          float float_2 = (float)sprite_1.getHeight() / (sprite_1.getMaxV() - sprite_1.getMinV());
	//          float float_3 = 4.0F / Math.max(float_2, float_1);
	//          float float_4 = (modelElementTexture_1.uvs[0] + modelElementTexture_1.uvs[0] + modelElementTexture_1.uvs[2] + modelElementTexture_1.uvs[2]) / 4.0F;
	//          float float_5 = (modelElementTexture_1.uvs[1] + modelElementTexture_1.uvs[1] + modelElementTexture_1.uvs[3] + modelElementTexture_1.uvs[3]) / 4.0F;
	//          modelElementTexture_1.uvs[0] = MathHelper.lerp(float_3, modelElementTexture_1.uvs[0], float_4);
	//          modelElementTexture_1.uvs[2] = MathHelper.lerp(float_3, modelElementTexture_1.uvs[2], float_4);
	//          modelElementTexture_1.uvs[1] = MathHelper.lerp(float_3, modelElementTexture_1.uvs[1], float_5);
	//          modelElementTexture_1.uvs[3] = MathHelper.lerp(float_3, modelElementTexture_1.uvs[3], float_5);
	//          int[] ints_1 = this.method_3458(modelElementTexture_1, sprite_1, direction_1, this.method_3459(vector3f_1, vector3f_2), modelRotationContainer_1.getRotation(), modelRotation_1, boolean_1);
	//          Direction direction_2 = method_3467(ints_1);
	//          System.arraycopy(floats_1, 0, modelElementTexture_1.uvs, 0, floats_1.length);
	//          if (modelRotation_1 == null) {
	//             this.method_3462(ints_1, direction_2);
	//          }
	//
	//          return new BakedQuad(ints_1, modelElementFace_1.tintIndex, direction_2, sprite_1);
	//       }
    }

    /**
     * Use when you know you are keeping a reference to the 
     * baked vertex data and aren't trying to avoid allocation overhead.<p>
     * 
     * Models that do dynamic baking should use {@link #outputBakedQuadData(int[], int)}
     * and manage their own allocations.
     */
    public final int[] outputBakedQuadData() {
	int[] result = new int[quadSize()];
	outputBakedQuadData(result, 0);
	return result;
    }

    /**
     * Handles serialization of quad-level information for baked quads.
     * Same representation is used for work-in-progress and for completed work.
     * Control information always lives at the first index of vertex data.
     */
    public static abstract class Quad
    {
	/**
	 * Fly-weight abstraction for small integer bitwise serialization. 
	 * More clear and less error-prone than hand-coding but not worth bringing in a new library.
	 */
	private static class Bits {
	    private static int totalBitLength;

	    private final int mask;
	    private final int shift;
	    private final int shiftedMask;
	    private final int shiftedInverseMask;

	    private Bits(int valueCount) {
		final int bitLength = Integer.SIZE - Integer.numberOfLeadingZeros(valueCount - 1);
		mask = (1 << (bitLength + 1)) - 1;
		shift = totalBitLength;
		shiftedMask = mask << shift;
		shiftedInverseMask = ~shiftedMask;
		totalBitLength += bitLength;
	    }

	    final int set(int i, int inBits) {
		return ((inBits & this.shiftedInverseMask) | get(i));
	    }

	    final int get(int fromBits) {
		return ((fromBits >> shift) & mask); 
	    }
	}

	static final private Bits TEXTURE_DEPTH  = new Bits(MAX_TEXTURE_DEPTH);
	static final private Bits LAYER_FLAGS  = new Bits(BlockRenderLayer.values().length);
	static final private Bits ENABLE_WORLD_DIFFUSE = new Bits(2);
	static final private Bits ENABLE_WORLD_AO = new Bits(2);
	static final private Bits ENABLE_LIGHTMAP_DIFFUSE = new Bits(2);
	static final private Bits ENABLE_LIGHTMAP_AO = new Bits(2);
	static final private Bits NOMINAL_FACE = new Bits(7);
	static final private Bits ACTUAL_FACE = new Bits(7);
	static final private Bits[] RENDER_LAYER = new Bits[MAX_TEXTURE_DEPTH];
	static final private Bits[] ENABLE_LIGHTMAP = new Bits[MAX_TEXTURE_DEPTH];
	static final private Bits[] ENABLE_BLOCK_TINT = new Bits[MAX_TEXTURE_DEPTH];

	static final int DEFAULT_CONTROL_BITS;

	static {
	    for(TextureDepth depth : TextureDepth.values()) {
		RENDER_LAYER[depth.ordinal()] = new Bits(4);
		ENABLE_LIGHTMAP[depth.ordinal()] = new Bits(2);
		ENABLE_BLOCK_TINT[depth.ordinal()] = new Bits(2);
	    }

	    int defaultBits = 0;
	    defaultBits = ENABLE_BLOCK_TINT[0].set(1, defaultBits);
	    defaultBits = ENABLE_BLOCK_TINT[1].set(1, defaultBits);
	    defaultBits = ENABLE_BLOCK_TINT[2].set(1, defaultBits);
	    defaultBits = ENABLE_WORLD_AO.set(1, defaultBits);
	    defaultBits = ENABLE_WORLD_DIFFUSE.set(1, defaultBits);

	    DEFAULT_CONTROL_BITS = defaultBits;
	}

	static final int CONTROL_FLAGS = 0;
	static final int LIGHTMAP = 1;
	static final int HEADER_STRIDE = 2;

	/** Directions by 1-based ordinal.  For efficient lookup. */
	private static final Direction[] DIRECTIONS_WITH_NONE = new Direction[7];
	static {
	    System.arraycopy(Direction.values(), 0, DIRECTIONS_WITH_NONE, 1, 6);
	}

	/** Local copy to avoid defensive array copying by JVM at run time due to concurrency. */
	private static final BlockRenderLayer[] LAYERS = BlockRenderLayer.values();

	public static void setEmissiveLightMap(int lightRGB, int[] vertexData, int index) {
	    vertexData[LIGHTMAP + index] = lightRGB;
	}

	public static int getEmissiveLightMap(int[] vertexData, int index) {
	    return vertexData[LIGHTMAP + index];
	}

	public static BlockRenderLayer getRenderLayer(TextureDepth textureLayer, int[] vertexData, int index) {
	    return LAYERS[RENDER_LAYER[textureLayer.ordinal()].get(vertexData[index])];
	}

	public static void setRenderLayer(TextureDepth textureLayer, BlockRenderLayer renderLayer, int[] vertexData, int index) {
	    vertexData[CONTROL_FLAGS] =  RENDER_LAYER[textureLayer.ordinal()].set(renderLayer.ordinal(), vertexData[index]);
	}

	/**
	 * Retrieves face direction from a baked quad. Returns null for quads not coplanar with a block face.
	 */
	public static Direction getActualFace(int[] vertexData, int index) {
	    return DIRECTIONS_WITH_NONE[ACTUAL_FACE.get(vertexData[index])];
	}

	public static void setActulFace(Direction face, int[] vertexData, int index) {
	    int faceIndex = face == null ? 0 : face.ordinal() + 1;
	    vertexData[index] = ACTUAL_FACE.set(faceIndex, vertexData[index]);
	}

	/**
	 * Face used for texture semantics. Always non-null.
	 */
	public static Direction getNominalFace(int[] vertexData, int index) {
	    return DIRECTIONS_WITH_NONE[1 + NOMINAL_FACE.get(vertexData[index])];
	}

	public static void setNominalFace(Direction face, int[] vertexData, int index) {
	    vertexData[index] = ACTUAL_FACE.set(1 + face.ordinal(), vertexData[index]);
	}

	/**
	 * Returns bit flags position-aligned to BlockRenderLayer ordinals indicating
	 * which block render layers are present in the given baked quad.
	 */
	public static int getExtantLayers(int[] vertexData, int index) {
	    return LAYER_FLAGS.get(vertexData[index]);
	}

	private static final TextureDepth[] DEPTHS = TextureDepth.values();
	public static TextureDepth getTextureDepth(int[] vertexData, int index) {
	    return DEPTHS[TEXTURE_DEPTH.get(vertexData[index])];
	}

	public static void setTextureDepth(TextureDepth depth, int[] vertexData, int index) {
	    vertexData[index] = TEXTURE_DEPTH.set(depth.ordinal(), vertexData[index]);
	}

	public static int quadSize(int[] vertexData, int index) {
	    return HEADER_STRIDE + Vertex.FIXED_QUAD_STRIDE + Vertex.LAYER_QUAD_STRIDE 
		    + Vertex.LAYER_QUAD_STRIDE * TEXTURE_DEPTH.get(vertexData[index]);
	}

	public static boolean getBlockColorEnabled(TextureDepth textureLayer, int[] vertexData, int index) {
	    return ENABLE_BLOCK_TINT[textureLayer.ordinal()].get(vertexData[index]) == 1;
	}

	public static void setBlockColorEnabled(TextureDepth textureLayer, boolean enabled, int[] vertexData, int index) {
	    vertexData[index] = ENABLE_BLOCK_TINT[textureLayer.ordinal()].set(enabled ? 1 : 0, vertexData[index]);
	}

	public static boolean getLightmapEnabled(TextureDepth textureLayer, int[] vertexData, int index) {
	    return ENABLE_LIGHTMAP[textureLayer.ordinal()].get(vertexData[index]) == 1;
	}

	public static void setLightmapEnabled(TextureDepth textureLayer, boolean enabled, int[] vertexData, int index) {
	    vertexData[index] = ENABLE_LIGHTMAP[textureLayer.ordinal()].set(enabled ? 1 : 0, vertexData[index]);
	}

	public static boolean getWorldDiffuseEnabled(int[] vertexData, int index) {
	    return ENABLE_WORLD_DIFFUSE.get(vertexData[index]) == 1;
	}

	public static void setWorldDiffuseEnabled(boolean enabled, int[] vertexData, int index) {
	    vertexData[index] = ENABLE_WORLD_DIFFUSE.set(enabled ? 1 : 0, vertexData[index]);
	}

	public static boolean getLightmapDiffuseEnabled(int[] vertexData, int index) {
	    return ENABLE_LIGHTMAP_DIFFUSE.get(vertexData[index]) == 1;
	}

	public static void setLightmapDiffuseEnabled(boolean enabled, int[] vertexData, int index) {
	    vertexData[index] = ENABLE_LIGHTMAP_DIFFUSE.set(enabled ? 1 : 0, vertexData[index]);
	}

	public static boolean getWorldAoEnabled(int[] vertexData, int index) {
	    return ENABLE_WORLD_AO.get(vertexData[index]) == 1;
	}

	public static void setWorldAoEnabled(boolean enabled, int[] vertexData, int index) {
	    vertexData[index] = ENABLE_WORLD_AO.set(enabled ? 1 : 0, vertexData[index]);
	}

	public static boolean getLightmapAoEnabled(int[] vertexData, int index) {
	    return ENABLE_LIGHTMAP_AO.get(vertexData[index]) == 1;
	}

	public static void setLightmapAoEnabled(boolean enabled, int[] vertexData, int index) {
	    vertexData[index] = ENABLE_LIGHTMAP_AO.set(enabled ? 1 : 0, vertexData[index]);
	}
    }

    /**
     * Handles serialization of quad-level information for baked quads.
     * Same representation is used for work-in-progress and for completed work.
     * Control information always lives at the first index of vertex data.
     */
    public static abstract class Vertex
    {
	// vertex attribute indexes relative first index after header
	// we want positions to be invariant for both WIP and baked quads
	// so invariant elements are packed first (all vertices)
	// followed by four vertices of UV/color data per layer
	static final int POS_X = 0;
	static final int POS_Y = 1;
	static final int POS_Z = 2;
	static final int NORM_X = 3;
	static final int NORM_Y = 4;
	static final int NORM_Z = 5;
	static final int COLOR_0 = 6;
	
	// layer attributes
	static final int LAYER_U = 0;
	static final int LAYER_V = 1;
	static final int LAYER_COLOR = 2;
	
	static final int FIXED_VERTEX_STRIDE = 6; // pos + normal
	static final int FIXED_QUAD_STRIDE = FIXED_VERTEX_STRIDE * 4;
	static final int LAYER_VERTEX_STRIDE = 3; // UV + color;
	static final int LAYER_QUAD_STRIDE = LAYER_VERTEX_STRIDE * 4;
	static final int FIRST_LAYER_INDEX = FIXED_VERTEX_STRIDE * 4 + Quad.HEADER_STRIDE;
	
	static final int MINIMUM_VERTEX_STRIDE = FIXED_VERTEX_STRIDE + LAYER_VERTEX_STRIDE;
	static final int MAX_VERTEX_STRIDE = FIXED_VERTEX_STRIDE + LAYER_VERTEX_STRIDE;

	public static final int MISSING_NORMAL = Float.floatToRawIntBits(Float.NaN);
	
	public static void position(int vertexIndex, float x, float y, float z, int[] vertexData, int index) {
	    final int baseIndex = index + Quad.HEADER_STRIDE + vertexIndex * FIXED_VERTEX_STRIDE;
	    vertexData[baseIndex + POS_X] = Float.floatToRawIntBits(x);
	    vertexData[baseIndex + POS_Y] = Float.floatToRawIntBits(y);
	    vertexData[baseIndex + POS_Z] = Float.floatToRawIntBits(z);
	}

	public static void normal(int vertexIndex, float x, float y, float z, int[] vertexData, int index) {
	    final int baseIndex = index + Quad.HEADER_STRIDE + vertexIndex * FIXED_VERTEX_STRIDE;
	    vertexData[baseIndex + NORM_X] = Float.floatToRawIntBits(x);
	    vertexData[baseIndex + NORM_Y] = Float.floatToRawIntBits(y);
	    vertexData[baseIndex + NORM_Z] = Float.floatToRawIntBits(z);
	}
	
	public static void clearNormals(int[] vertexData, int index) {
	    for(int i = 0; i < 4; i++)
		normal(i, MISSING_NORMAL, MISSING_NORMAL, MISSING_NORMAL, vertexData, index);
	}
	
	public static void uv(int vertexIndex, TextureDepth textureDepth, float u, float v, int[] vertexData, int index) {
	    final int baseIndex = index + FIRST_LAYER_INDEX
		+ LAYER_QUAD_STRIDE * textureDepth.ordinal()
	    	+ LAYER_VERTEX_STRIDE * vertexIndex;
	    vertexData[baseIndex + LAYER_U] = Float.floatToRawIntBits(u);
	    vertexData[baseIndex + LAYER_V] = Float.floatToRawIntBits(v);
	}
	
	public static void color(int vertexIndex, TextureDepth textureDepth, int color, int[] vertexData, int index) {
	    final int colorIndex = index + FIRST_LAYER_INDEX + LAYER_COLOR
		+ LAYER_QUAD_STRIDE * textureDepth.ordinal()
	    	+ LAYER_VERTEX_STRIDE * vertexIndex;
	    vertexData[colorIndex] = color;
	}
    }

    static final int MAX_TEXTURE_DEPTH = TextureDepth.values().length;
    private static final int MAX_QUAD_STRIDE = Quad.HEADER_STRIDE + Vertex.MAX_VERTEX_STRIDE * 4;
}
