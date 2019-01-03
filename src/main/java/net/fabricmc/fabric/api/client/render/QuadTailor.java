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

import static net.minecraft.util.math.MathHelper.equalsApproximate;

import net.minecraft.client.render.block.BlockRenderLayer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;

/**
 * Generates vertex data needed to implement FabricBakedQuad.<p>
 * 
 * "Quad Baking" is mostly about assigning renderable texture coordinates.
 * We also assign missing vertex normals. When this happens we do not 
 * know where the model will be rendered, so we cannot complete 
 * lighting calculations but we can analyze geometry and capture
 * information that can be reused when the qauds are lit later on.<p>
 * 
 * For conventional quads, render results will be identical to normal Minecraft. However,
 * Fabric Quads support the following additional features:<p>
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
public final class QuadTailor {

	private final int[] vertexData = new int[MAX_QUAD_STRIDE];
	private final Sprite[] sprites = new Sprite[3];
	private int textureBits = Texture.DEFAULT_TEXTURE_BITS;

	public QuadTailor() {
		clear();
	}

	/**
	 * Controls alpha and mip-map settings for each texture layer via BlockRenderLayer.<p>
	 * 
	 * Some restrictions apply:<p>
	 * <li>If SOLID occurs it must be first. Only one SOLID layer is possible. (Anything else would be pointless.)</li>
	 * <li>TRANSLUCENT layers must be "above" SOLID and CUTOUT layers.</li><p>
	 * 
	 * Block Entity Renderer quads have an additional restriction:
	 * If TRANSLUCENT occurs, then SOLID must also occur (as the first layer).
	 * This is necessary because transparency sort will is not feasible for
	 * dynamic rendering, but translucent overlay textures on solid quads 
	 * can be rendered correctly without sorting. <p>
	 * 
	 * Lastly, note that CUTOUT textures are likely to cause Z fighting if a solid
	 * texture or other CUTOUT textures are also present. This class does not try
	 * to prevent Z-fighting by "bumping" overlay layers out because that approach
	 * is unreliable at longer render distances due to the single precision floating 
	 * point calculations used in the GPU.<p>
	 * 
	 * To avoid Z-fighting with multiple texture layers, use CUTOUT textures that do 
	 * not overlap, or use a SOLID base texture with one or two TRANSLUCENT overlay textures.<p>
	 * 
	 * Add-on rendering mods that implement blending in shaders may render SOLID-CUTOUT
	 * overlays correctly, but no check for that is made here.
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
		Vertex.setPos(vertexIndex, x, y, z, vertexData, 0);
	}

	/**
	 * Sets a vertex normal at the given vertex index. Most models don't have
	 * or need vertex normals.  If omitted, computed face normal will be used.
	 */
	public final void normal(int vertexIndex, float x, float y, float z) {
		Vertex.setNormal(vertexIndex, x, y, z, vertexData, 0);
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
	 * Facing of this quad for texturing purposes. Has no
	 * effect on lighting, but is used to interpret texture rotation. <p>
	 * 
	 * Null by default.  If not assigned, will be inferred 
	 * from quad geometry.  This can be ambiguous for wedges,
	 * cylinders or other non-cubic shapes, so it best to assign it.
	 */
	public final void setNominalFace(Direction face) {
		Quad.setNominalFace(face, vertexData, 0);
	}

	/**
	 * Assigns given texture atlas sprite to texture layer. 
	 * Textures outside the texture atlas are unsupported. 
	 */
	public final void setSprite(TextureDepth textureDepth, Sprite sprite) {
		sprites[textureDepth.ordinal()] = sprite;
	}

	/**
	 * Causes texture for the given layer to appear rotated,
	 * relative to nominal face. (See {@link #setNominalFace(Direction)})<p>
	 * 
	 * Conventions for what "unrotated" means are the same as 
	 * for conventional Minecraft JSON models.<p>
	 *
	 * 0 = no rotation, 1 = 90 degrees, 2 = 180, 3 = 270.
	 * Values outside the 0-3 range wrap. (Does modulo for you.)
	 * are handled via modulo division.  Zero by default.
	 */
	public final void setRotation(TextureDepth textureDepth, int rotation) {
		textureBits = Texture.setRotation(textureBits, textureDepth, rotation);
	}

	/**
	 * When enabled, texture coordinate for the given layer are 
	 * assigned based on vertex position and uv coordinates set by
	 * {@link #uv(int, TextureDepth, float, float)} are ignored.<p>
	 * 
	 * Enabled by default, because most models are textured this way.
	 * However, setting any UV coordinate via {@link #uv(int, TextureDepth, float, float)}
	 * disables this feature automatically for the involved layer.<p>
	 * 
	 * UV lock always derives texture coordinates based on nominal face, even
	 * when the quad is not co-planar with that face, and the result is
	 * the same as if the quad were projected onto the nominal face, which
	 * is usually the desired result.<p>
	 */
	public final void enableLockUV(TextureDepth textureDepth, boolean enable) {
		textureBits = Texture.setLockUV(textureBits, textureDepth, enable);
	}

	/**
	 * When enabled, U texture coordinates for the given layer are 
	 * flipped as part of baking. Can be useful for some randomization
	 * and texture mapping scenarios. Results are different than what
	 * can be obtained via rotation and both can be applied.<p>
	 * 
	 * Disabled by default and UV lock must be disabled for this feature
	 * to work.
	 */
	public final void enableFlipU(TextureDepth textureDepth, boolean enable) {
		textureBits = Texture.enableFlipU(textureBits, textureDepth, enable);
	}
	
	/**
	 * Same as {@link #enableFlipU(TextureDepth, boolean)} but for V coordinate.
	 */
	public final void enableFlipV(TextureDepth textureDepth, boolean enable) {
		textureBits = Texture.enableFlipV(textureBits, textureDepth, enable);
	}
	
	/**
	 * UV coordinates by default are assumed to be 0-16 scale for consistency
	 * with conventional Minecraft model format. This is scaled to 0-1 during
	 * baking before interpolation. Model loaders that already have 0-1 coordinates
	 * can avoid wasteful multiplication/division by passing 0-1 coordinates directly.<p>
	 * 
	 * Enabled by default.
	 */
	public final void enableUVScale(TextureDepth textureDepth, boolean enable) {
		textureBits = Texture.enableUVScale(textureBits, textureDepth, enable);
	}
	
	/**
	 * Disables all texture transformation for the given layer and uses provided
	 * texture coordinates as-is. This means caller is responsible for handling 
	 * texture map interpolation. <p>
	 * 
	 * Useful for "wrapping" of previously baked quads for overlay by other layers.
	 */
	public final void enableRawUV(TextureDepth textureDepth, boolean enable) {
		textureBits = Texture.enableRawUV(textureBits, textureDepth, enable);
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
		Vertex.setUV(vertexIndex, textureDepth, u, v, vertexData, 0);
		
		// implies we don't want to derive uv from geometry
		enableLockUV(textureDepth, false);
	}

	/**
	 * Assigns color to vertex for the given texture layer.  Texture pixel colors will be multiplied
	 * by this color.  The alpha component is only used for TRANSLUCENT layer rendering.<p>
	 * 
	 * Colors low byte should be red, followed by green and blue, and alpha as the high byte. 
	 * So for example, pure red opaque green is 0xFF00FF00;<p>
	 * 
	 * Default value for all layers is white. (0xFFFFFFFF)
	 */
	public final void color(int vertexIndex, TextureDepth textureDepth, int color) {
		Vertex.setColor(vertexIndex, textureDepth, color, vertexData, 0);
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
	public final void setEmissiveLightMap(int vertexIndex, int lightRGB) {
		Vertex.setEmissiveLightMap(vertexIndex, lightRGB, vertexData, 0);
	}

	/**
	 * Version of {@link #setEmissiveLightMap(int)} that accepts unpacked int components.
	 */
	public final void setEmissiveLightMap(int vertexIndex, int red, int green, int blue) {
		setEmissiveLightMap(vertexIndex, red | (green << 8) | (blue << 16));
	}

	/**
	 * Version of {@link #setEmissiveLightMap(int)} that accepts unpacked float components.
	 */
	public final void setEmissiveLightMap(int vertexIndex, float red, float green, float blue) {
		setEmissiveLightMap(vertexIndex, Math.round(red * 255), Math.round(green * 255), Math.round(blue * 255));
	}

	/**
	 * Reset all lightmaps to default (full brightness).
	 */
	public final void clearEmissiveLightMap() {
		Vertex.clearLightmaps(vertexData, 0);
	}
	
	/**
	 * Returns all settings to default values.
	 */
	public final void clear() {
		sprites[0] = null;
		sprites[1] = null;
		sprites[2] = null;
		textureBits = Texture.DEFAULT_TEXTURE_BITS;
		vertexData[0] = Quad.DEFAULT_CONTROL_BITS;
		clearEmissiveLightMap();
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
		return Bakery.outputBakedQuadData(vertexData, 0, sprites, target, start, textureBits);
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
		static final int HEADER_STRIDE = 1;

		/** Directions by 1-based ordinal.  For efficient lookup. */
		private static final Direction[] DIRECTIONS_WITH_NONE = new Direction[7];
		static {
			System.arraycopy(Direction.values(), 0, DIRECTIONS_WITH_NONE, 1, 6);
		}

		/** Local copy to avoid defensive array copying by JVM at run time due to concurrency. */
		private static final BlockRenderLayer[] LAYERS = BlockRenderLayer.values();

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
		 * Face used for texture semantics.
		 */
		public static Direction getNominalFace(int[] vertexData, int index) {
			return DIRECTIONS_WITH_NONE[NOMINAL_FACE.get(vertexData[index])];
		}

		public static void setNominalFace(Direction face, int[] vertexData, int index) {
			int faceIndex = face == null ? 0 : face.ordinal() + 1;
			vertexData[index] = NOMINAL_FACE.set(faceIndex, vertexData[index]);
		}

		/**
		 * Returns bit flags position-aligned to BlockRenderLayer ordinals indicating
		 * which block render layers are present in the given baked quad.
		 */
		public static int getExtantLayers(int[] vertexData, int index) {
			return LAYER_FLAGS.get(vertexData[index]);
		}

		private static final TextureDepth[] DEPTHS = TextureDepth.values();
		
		public static int getTextureDepthOrdinal(int[] vertexData, int index) {
			return TEXTURE_DEPTH.get(vertexData[index]);
		}
		
		public static TextureDepth getTextureDepth(int[] vertexData, int index) {
			return DEPTHS[getTextureDepthOrdinal(vertexData, index)];
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
		static final int LIGHTMAP = 6;


		// layer attributes
		static final int LAYER_U = 0;
		static final int LAYER_V = 1;
		static final int LAYER_COLOR = 2;

		static final int FIXED_VERTEX_STRIDE = 7; // pos + normal + lightmap
		static final int FIXED_QUAD_STRIDE = FIXED_VERTEX_STRIDE * 4;
		static final int LAYER_VERTEX_STRIDE = 3; // UV + color;
		static final int LAYER_QUAD_STRIDE = LAYER_VERTEX_STRIDE * 4;
		static final int FIRST_LAYER_INDEX = FIXED_VERTEX_STRIDE * 4 + Quad.HEADER_STRIDE;

		static final int MINIMUM_VERTEX_STRIDE = FIXED_VERTEX_STRIDE + LAYER_VERTEX_STRIDE;
		static final int MAX_VERTEX_STRIDE = FIXED_VERTEX_STRIDE + LAYER_VERTEX_STRIDE;

		public static final int MISSING_NORMAL = Float.floatToRawIntBits(Float.NaN);

		public static void setPos(int vertexIndex, float x, float y, float z, int[] vertexData, int index) {
			final int baseIndex = index + Quad.HEADER_STRIDE + vertexIndex * FIXED_VERTEX_STRIDE;
			vertexData[baseIndex + POS_X] = Float.floatToRawIntBits(x);
			vertexData[baseIndex + POS_Y] = Float.floatToRawIntBits(y);
			vertexData[baseIndex + POS_Z] = Float.floatToRawIntBits(z);
		}

		public static float getPosX(int vertexIndex, int[] vertexData, int index) {
			return Float.intBitsToFloat(vertexData[index + Quad.HEADER_STRIDE + vertexIndex * FIXED_VERTEX_STRIDE + POS_X]);
		}

		public static float getPosY(int vertexIndex, int[] vertexData, int index) {
			return Float.intBitsToFloat(vertexData[index + Quad.HEADER_STRIDE + vertexIndex * FIXED_VERTEX_STRIDE + POS_Y]);
		}

		public static float getPosZ(int vertexIndex, int[] vertexData, int index) {
			return Float.intBitsToFloat(vertexData[index + Quad.HEADER_STRIDE + vertexIndex * FIXED_VERTEX_STRIDE + POS_Z]);
		}

		public static void setNormal(int vertexIndex, float x, float y, float z, int[] vertexData, int index) {
			final int baseIndex = index + Quad.HEADER_STRIDE + vertexIndex * FIXED_VERTEX_STRIDE;
			vertexData[baseIndex + NORM_X] = Float.floatToRawIntBits(x);
			vertexData[baseIndex + NORM_Y] = Float.floatToRawIntBits(y);
			vertexData[baseIndex + NORM_Z] = Float.floatToRawIntBits(z);
		}

		public static void setNormalIfMissing(int vertexIndex, float x, float y, float z, int[] vertexData, int index) {
			final int baseIndex = index + Quad.HEADER_STRIDE + vertexIndex * FIXED_VERTEX_STRIDE;
			if(vertexData[baseIndex + NORM_X] == MISSING_NORMAL) {
				vertexData[baseIndex + NORM_X] = Float.floatToRawIntBits(x);
				vertexData[baseIndex + NORM_Y] = Float.floatToRawIntBits(y);
				vertexData[baseIndex + NORM_Z] = Float.floatToRawIntBits(z);
			}
		}

		public static float getNormX(int vertexIndex, int[] vertexData, int index) {
			return Float.intBitsToFloat(vertexData[index + Quad.HEADER_STRIDE + vertexIndex * FIXED_VERTEX_STRIDE + NORM_X]);
		}

		public static float getNormY(int vertexIndex, int[] vertexData, int index) {
			return Float.intBitsToFloat(vertexData[index + Quad.HEADER_STRIDE + vertexIndex * FIXED_VERTEX_STRIDE + NORM_Y]);
		}

		public static float getNormZ(int vertexIndex, int[] vertexData, int index) {
			return Float.intBitsToFloat(vertexData[index + Quad.HEADER_STRIDE + vertexIndex * FIXED_VERTEX_STRIDE + NORM_Z]);
		}

		public static void clearNormals(int[] vertexData, int index) {
			for(int i = 0; i < 4; i++)
				setNormal(i, MISSING_NORMAL, MISSING_NORMAL, MISSING_NORMAL, vertexData, index);
		}

		public static void setEmissiveLightMap(int vertexIndex, int lightRGB, int[] vertexData, int index) {
			vertexData[index + Quad.HEADER_STRIDE + vertexIndex * FIXED_VERTEX_STRIDE + LIGHTMAP] = lightRGB;
		}

		public static int getEmissiveLightMap(int vertexIndex, int[] vertexData, int index) {
			return vertexData[index + Quad.HEADER_STRIDE + vertexIndex * FIXED_VERTEX_STRIDE + LIGHTMAP];
		}
		
		public static void clearLightmaps(int[] vertexData, int index) {
			for(int i = 0; i < 4; i++)
				setEmissiveLightMap(i, 0xFFFFFF, vertexData, index);
		}

		public static void setUV(int vertexIndex, TextureDepth textureDepth, float u, float v, int[] vertexData, int index) {
			setUV(vertexIndex, textureDepth.ordinal(), u, v, vertexData, index);
		}

		public static void setUV(int vertexIndex, int textureDepthOrdinal, float u, float v, int[] vertexData, int index) {
			final int baseIndex = index + FIRST_LAYER_INDEX
					+ LAYER_QUAD_STRIDE * textureDepthOrdinal
					+ LAYER_VERTEX_STRIDE * vertexIndex;
			vertexData[baseIndex + LAYER_U] = Float.floatToRawIntBits(u);
			vertexData[baseIndex + LAYER_V] = Float.floatToRawIntBits(v);
		}
		
		public static float getU(int vertexIndex, TextureDepth textureDepth, int[] vertexData, int index) {
			return getU(vertexIndex, textureDepth.ordinal(), vertexData, index);
		}

		public static float getU(int vertexIndex, int textureDepthOrdinal, int[] vertexData, int index) {
			return Float.intBitsToFloat(vertexData[index + FIRST_LAYER_INDEX + LAYER_U
			    + LAYER_QUAD_STRIDE * textureDepthOrdinal + LAYER_VERTEX_STRIDE * vertexIndex]);
		}
		
		public static float getV(int vertexIndex, TextureDepth textureDepth, int[] vertexData, int index) {
			return getV(vertexIndex, textureDepth.ordinal(), vertexData, index);
		}
		
		public static float getV(int vertexIndex, int textureDepthOrdinal, int[] vertexData, int index) {
			return Float.intBitsToFloat(vertexData[index + FIRST_LAYER_INDEX + LAYER_V
			    + LAYER_QUAD_STRIDE * textureDepthOrdinal + LAYER_VERTEX_STRIDE * vertexIndex]);
		}

		public static void setColor(int vertexIndex, TextureDepth textureDepth, int color, int[] vertexData, int index) {
			final int colorIndex = index + FIRST_LAYER_INDEX + LAYER_COLOR
				+ LAYER_QUAD_STRIDE * textureDepth.ordinal()
				+ LAYER_VERTEX_STRIDE * vertexIndex;
			vertexData[colorIndex] = color;
		}

		private static final float NORMALIZE_MULTIPLIER = 1f / 16f;
		
		/** Scales from 0-16 to 0-1 */
		public static void normalizeUV(int layer, int[] vertexData, int baseIndex) {
			for(int v = 0; v < 4; v++) 
				setUV(v, layer, 
						NORMALIZE_MULTIPLIER * getU(v, layer, vertexData, baseIndex), 
						NORMALIZE_MULTIPLIER * getV(v, layer, vertexData, baseIndex), 
						vertexData, baseIndex);
		}
		
		/** Inverts U coordinates.  Assumes normalized (0-1) values. */
		public static void flipUV(int layer, int[] vertexData, int baseIndex, boolean flipU, boolean flipV) {
			for(int i = 0; i < 4; i++) {
				final float u = flipU ? 1 - getU(i, layer, vertexData, baseIndex) : getU(i, layer, vertexData, baseIndex);
				final float v = flipV ? 1 - getV(i, layer, vertexData, baseIndex) : getV(i, layer, vertexData, baseIndex);
				setUV(i, layer, u, v, vertexData, baseIndex);
			}
		}
	}

	/**
	 * Serialization methods for texture parameters.
	 * These aren't stored with the baked quad because
	 * baking tranlates them to texture atlas coordinates. <p>
	 * 
	 * Public to support static implementations.
	 * All elements are packed into an int word, but other 
	 * implementation details are private to enable non-breaking changes.<p>
	 */
	public static abstract class Texture
	{
		// LOCK_UV_SHIFT = 0;
		private static final int RAW_UV_SHIFT = MAX_TEXTURE_DEPTH;
		private static final int UV_SCALE_SHIFT = RAW_UV_SHIFT + MAX_TEXTURE_DEPTH;
		private static final int FLIP_U_SHIFT =   UV_SCALE_SHIFT + MAX_TEXTURE_DEPTH;
		private static final int FLIP_V_SHIFT =   FLIP_U_SHIFT + MAX_TEXTURE_DEPTH;
		private static final int ROTATION_SHIFT = FLIP_V_SHIFT + MAX_TEXTURE_DEPTH;

		/** Lock-UV enabled & no rotation for all layers */
		public static final int DEFAULT_TEXTURE_BITS = 0b111;

		public static int enableRawUV(int priorBits, TextureDepth layer, boolean enable) {
			final int mask = 1 << (RAW_UV_SHIFT + layer.ordinal());
			return enable ? (priorBits | mask) : (priorBits & ~mask);
		}

		public static boolean isRawUV(int textureBits, int layerOrdinal) {
			return (textureBits & (1 << (layerOrdinal + RAW_UV_SHIFT))) != 0;
		}
		
		public static int enableUVScale(int priorBits, TextureDepth layer, boolean enable) {
			final int mask = 1 << (UV_SCALE_SHIFT + layer.ordinal());
			return enable ? (priorBits | mask) : (priorBits & ~mask);
		}

		public static boolean isUVScaleEnabled(int textureBits, int layerOrdinal) {
			return (textureBits & (1 << (layerOrdinal + UV_SCALE_SHIFT))) != 0;
		}
		
		public static int enableFlipU(int priorBits, TextureDepth layer, boolean enable) {
			final int mask = 1 << (FLIP_U_SHIFT + layer.ordinal());
			return enable ? (priorBits | mask) : (priorBits & ~mask);
		}
		
		public static boolean isFlipU(int textureBits, int layerOrdinal) {
			return (textureBits & (1 << (layerOrdinal + FLIP_U_SHIFT))) != 0;
		}
		
		public static int enableFlipV(int priorBits, TextureDepth layer, boolean enable) {
			final int mask = 1 << (FLIP_V_SHIFT + layer.ordinal());
			return enable ? (priorBits | mask) : (priorBits & ~mask);
		}

		public static boolean isFlipV(int textureBits, int layerOrdinal) {
			return (textureBits & (1 << (layerOrdinal + FLIP_V_SHIFT))) != 0;
		}
		
		static int setRotation(int priorBits, TextureDepth layer, int rotation)
		{
			final int shift = ROTATION_SHIFT + layer.ordinal() * 2;
			return (priorBits & ~(3 << shift)) | ((rotation % 4) << shift);
		}
		
		static int getRotation(int textureBits, int layerOrdinal) {
			final int shift = ROTATION_SHIFT + layerOrdinal * 2;
			return (textureBits >> shift) & 3;
		}
		
		static int setLockUV(int priorBits, TextureDepth layer, boolean enable) {
			final int mask = 1 << layer.ordinal();
			return enable ? (priorBits | mask) : (priorBits & ~mask);
		}

		static boolean isLockUV(int textureBits, int layerOrdinal) {
			return (textureBits & (1 << layerOrdinal)) != 0;
		}

		static int setAll(int rotation0, boolean lockUV0, int rotation1, boolean lockUV1, int rotation2, boolean lockUV2) {
			return (lockUV0 ? 1 : 0)
					|  (lockUV1 ? 2 : 0)
					|  (lockUV2 ? 4 : 0)
					|  ((rotation0 % 4) << ROTATION_SHIFT)
					|  ((rotation1 % 4) << (ROTATION_SHIFT + 2))
					|  ((rotation2 % 4) << (ROTATION_SHIFT + 4));
		}
	}

	static final int MAX_TEXTURE_DEPTH = TextureDepth.values().length;
	private static final int MAX_QUAD_STRIDE = Quad.HEADER_STRIDE + Vertex.MAX_VERTEX_STRIDE * 4;

	/**
	 * Holds the static methods used for baking.
	 */
	static abstract class Bakery
	{
		static final float EPSILON_MIN = 1.0E-4F;
		static final float EPSILON_MAX = 0.9999F;
		/**
		 * Underlying static implementation of {@link QuadTailor#outputBakedQuadData(int[], int)}
		 * Requires an array of sprites, because those aren't serialized with the vertex data.<p>
		 * 
		 * Does not change data in source array.
		 * 
		 * @param source		Array containing quad data packed via other methods in this class.
		 * @param sourceStart	Index in source array where quad data starts.
		 * @param sprites		Array of texture sprites containing the sprite for each active layer.
		 * @param target		Array to receive baked vertex data. Does NOT check for space. Do before calling.
		 * @param targetStart	Index in target array to start writing.
		 * @param textureBits	Texture settings packed via {@link Texture}.  Or use {@link Texture#DEFAULT_TEXTURE_BITS} if that works.
		 * @return Number of int values written to target array.
		 */
		static int outputBakedQuadData(int[] source, int sourceStart, Sprite[] sprites, int[] target, int targetStart, int textureBits) {

			// we don't want to change the source state, so start by copying
			// to target, and then use target for remaining operations.
			final int quadSize = Quad.quadSize(source, targetStart);
			System.arraycopy(source, sourceStart, target, targetStart, quadSize);

			final float x0 = Vertex.getPosX(0, target, targetStart);
			final float y0 = Vertex.getPosY(0, target, targetStart);
			final float z0 = Vertex.getPosZ(0, target, targetStart);
			final float x1 = Vertex.getPosX(1, target, targetStart);
			final float y1 = Vertex.getPosY(1, target, targetStart);
			final float z1 = Vertex.getPosZ(1, target, targetStart);
			final float x2 = Vertex.getPosX(2, target, targetStart);
			final float y2 = Vertex.getPosY(2, target, targetStart);
			final float z2 = Vertex.getPosZ(2, target, targetStart);
			final float x3 = Vertex.getPosX(3, target, targetStart);
			final float y3 = Vertex.getPosY(3, target, targetStart);
			final float z3 = Vertex.getPosZ(3, target, targetStart);

			// compute our face normal - verbose to put all here but allows us to use primitives only
			float normX, normY, normZ;
			{
				final float dx0 = x0 - x1;
				final float dy0 = y0 - y1;
				final float dz0 = z0 - z1;
				final float dx1 = x2 - x0;
				final float dy1 = y2 - y0;
				final float dz1 = z2 - z0;

				normX = dy0 * dz1 - dy1 * dz0;
				normY = dx0 * dz1 - dx1 * dz0;
				normZ = dx0 * dy1 - dx1 * dy0;

				float l = (float) Math.sqrt(normX * normX + normY * normY + normZ * normZ);
				if(l != 0) {
					normX /= l;
					normY /= l;
					normZ /= l;
				}
			}

			populateMissingNormals(target, targetStart, normX, normY, normZ);

			// Analyze geometry and plug nominal face if missing.
			// Other bits are needed during lighting for face culling and shading.

			Direction geometricFace = null;
			boolean isOnBlockFace = false;

			switch(longestAxis(normX, normY, normZ)) {
				case X: {
					final float minX = min(x0, x1, x2, x3);
					final float maxX = max(x0, x1, x2, x3);
					boolean onPlane = equalsApproximate(minX, maxX);
					if(normX > 0) {
						geometricFace = Direction.EAST;
						isOnBlockFace = onPlane && maxX >= EPSILON_MAX;
					} else {
						geometricFace = Direction.WEST;
						isOnBlockFace = onPlane && minX <= EPSILON_MIN;
					}
					break;
				}
				case Y: {
					final float minY = min(y0, y1, y2, y3);
					final float maxY = max(y0, y1, y2, y3);
					boolean onPlane = equalsApproximate(minY, maxY);
					if(normY > 0) {
						geometricFace = Direction.UP;
						isOnBlockFace = onPlane && maxY >= EPSILON_MAX;
					} else {
						geometricFace = Direction.DOWN;
						isOnBlockFace = onPlane && minY <= EPSILON_MIN;
					}
					break;
				}
				case Z: {
					final float minZ = min(z0, z1, z2, z3);
					final float maxZ = max(z0, z1, z2, z3);
					boolean onPlane = equalsApproximate(minZ, maxZ);
					if(normZ > 0) {
						geometricFace = Direction.SOUTH;
						isOnBlockFace = onPlane && maxZ >= EPSILON_MAX;
					} else {
						geometricFace = Direction.NORTH;
						isOnBlockFace = onPlane && minZ <= EPSILON_MIN;
					}
					break;
				}
			}

			Quad.setActulFace(isOnBlockFace ? geometricFace : null, target, targetStart);

			Direction nominalFace = Quad.getNominalFace(target, targetStart);
			if(nominalFace == null) {
				nominalFace = geometricFace;
				Quad.setNominalFace(nominalFace, target, targetStart);
			}

			// The main event - texturing
			bakeTexturesForLayer(0, sprites[0], target, targetStart, textureBits);
			final int depth = Quad.getTextureDepthOrdinal(target, targetStart);
			if(depth > 1) {
				bakeTexturesForLayer(1, sprites[1], target, targetStart, textureBits);
				if(depth == 3)
					bakeTexturesForLayer(2, sprites[2], target, targetStart, textureBits);
			}

			return quadSize;
		}

		
		/**
		 * Handles all texture transformation and interpolation. 
		 */
		static private void bakeTexturesForLayer(int layer, Sprite sprite, int[] vertexData, int baseIndex, int textureBits)
		{
			// honor pre-baked UVs if requested
			if(Texture.isRawUV(textureBits, layer))
				return;
			
			// Normalize to 0-1 if we are getting 0-16, for sake of sanity
			if(Texture.isUVScaleEnabled(textureBits, layer))
				Vertex.normalizeUV(layer, vertexData, baseIndex);
			
			// handle lock UV
			if(Texture.isLockUV(textureBits, layer)) {
				UVLocker locker = UVLOCKERS[Quad.getNominalFace(vertexData, baseIndex).ordinal()];
				locker.apply(0, layer, vertexData, baseIndex);
				locker.apply(1, layer, vertexData, baseIndex);
				locker.apply(2, layer, vertexData, baseIndex);
				locker.apply(3, layer, vertexData, baseIndex);
			}

			// handle texture rotation
			applyTextureRotation(Texture.getRotation(textureBits, layer), layer, vertexData, baseIndex);
			
			// handle texture flip
			final boolean flipU = Texture.isFlipU(textureBits, layer);
			final boolean flipV = Texture.isFlipV(textureBits, layer);
			if(flipU || flipV)
				Vertex.flipUV(layer, vertexData, baseIndex, flipU, flipV);
			
	        // TODO: prevent bleeding / holes?

	        // final interpolation
	        final float spriteMinU = sprite.getMinU();
	        final float spriteSpanU = sprite.getMaxU() - spriteMinU;
	        final float spriteMinV = sprite.getMinV();
	        final float spriteSpanV = sprite.getMaxV() - spriteMinV;
			
	        // Doing it here faster than calling sprite methods
	        // They compute span each call and normalize inputs, 
	        // so we'd have to rescale before we called, only 
	        // to have the sprite renormalize immediately.
	        for(int i = 0; i < 4; i++)
	        	Vertex.setUV(i, layer, 
	        			spriteMinU + Vertex.getU(i, layer, vertexData, baseIndex) * spriteSpanU, 
	        			spriteMinV + Vertex.getV(i, layer, vertexData, baseIndex) * spriteSpanV, 
	        			vertexData, baseIndex);
            
		}
	    
		/** 
		 * Rotates texture around the center of sprite.
		 * Assumes normalized coordinates.
		 * */
	    private static void applyTextureRotation(int rotation, int layer, int[] vertexData, int baseIndex)
	    {
	       switch(rotation)
	       {
	       case 0: // ROTATE_NONE
	       default:
	           break;
	           
	       case 1: //ROTATE_90
	           for(int i = 0; i < 4; i++) {
	        	   float uNew = Vertex.getV(i, layer, vertexData, baseIndex);
	               float vNew = Vertex.getU(i, layer, vertexData, baseIndex);
	               Vertex.setUV(i, layer, uNew, vNew, vertexData, baseIndex);
	           }
	           break;

	       case 2: //ROTATE_180
	           for(int i = 0; i < 4; i++) {
	               float uNew = 1 - Vertex.getU(i, layer, vertexData, baseIndex);
	               float vNew = 1 - Vertex.getV(i, layer, vertexData, baseIndex);
	               Vertex.setUV(i, layer, uNew, vNew, vertexData, baseIndex);
	           }
	           break;
	       
	       case 3: //ROTATE_270
	           for(int i = 0; i < 4; i++) {
	               float vNew = Vertex.getU(i, layer, vertexData, baseIndex);
	               float uNew = 1 - Vertex.getV(i, layer, vertexData, baseIndex);
	               Vertex.setUV(i, layer, uNew, vNew, vertexData, baseIndex);
	           }
	        break;
	       
	       }
	    }
	    
		static private Axis longestAxis(float faceNormX, float faceNormY, float faceNormZ) {
			Axis result = Axis.Y;
			float longest = Math.abs(faceNormY);

			float a = Math.abs(faceNormX);
			if(a > longest)
			{
				result = Axis.X;
				longest = a;
			}

			return Math.abs(faceNormZ) > longest
					? Axis.Z : result;
		}

		static private void populateMissingNormals(int[] target, int targetStart, float normX, float normY, float normZ) {
			for(int i = 0; i < 4; i++)
				Vertex.setNormalIfMissing(i, normX, normY, normZ, target, targetStart);
		}

		static private float min(float a, float b, float c, float d) {
			return Math.min(Math.min(a, b), Math.min(d, c));
		}

		static private float max(float a, float b, float c, float d) {
			return Math.max(Math.max(a, b), Math.max(d, c));
		}
	}
	
	@FunctionalInterface
    private static interface UVLocker {
        void apply(int vertexIndex, int layerIndex, int[] vertexData, int baseIndex);
    }
    
    private static final UVLocker [] UVLOCKERS = new UVLocker[6];
    
    static {
        UVLOCKERS[Direction.EAST.ordinal()] = (v, l, d, b) -> 
        	Vertex.setUV(v, l, 1 - Vertex.getPosZ(v, d, b), 1 - Vertex.getPosY(v, d, b), d, b);
        UVLOCKERS[Direction.WEST.ordinal()] = (v, l, d, b) -> 
        	Vertex.setUV(l, v, Vertex.getPosZ(v, d, b), 1 - Vertex.getPosY(v, d, b), d, b);
        UVLOCKERS[Direction.NORTH.ordinal()] = (v, l, d, b) -> 
        	Vertex.setUV(l, v, 1 - Vertex.getPosX(v, d, b), 1 - Vertex.getPosY(v, d, b), d, b);
        UVLOCKERS[Direction.SOUTH.ordinal()] = (v, l, d, b) -> 
        	Vertex.setUV(l, v, Vertex.getPosX(v, d, b), 1 - Vertex.getPosY(v, d, b), d, b);
        UVLOCKERS[Direction.DOWN.ordinal()] = (v, l, d, b) -> 
        	Vertex.setUV(l, v, Vertex.getPosX(v, d, b), 1 - Vertex.getPosZ(v, d, b), d, b);
        // TODO: confirm matches MC default semantic - believe mine was flipped and so changed it to match
        UVLOCKERS[Direction.UP.ordinal()] = (v, l, d, b) -> 
        	Vertex.setUV(l, v, Vertex.getPosX(v, d, b), 1 - Vertex.getPosZ(v, d, b), d, b);
    }
}
