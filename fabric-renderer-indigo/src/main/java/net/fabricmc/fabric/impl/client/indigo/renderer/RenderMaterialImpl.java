/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
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

package net.fabricmc.fabric.impl.client.indigo.renderer;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.block.BlockRenderLayer;

import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;

/**
 * Default implementation of the standard render materials.
 * The underlying representation is simply an int with bit-wise
 * packing of the various material properties. This offers
 * easy/fast interning via int/object hashmap.
 */
public abstract class RenderMaterialImpl {
	/** zero position (default value) will be NULL. */
	private static final BlockRenderLayer[] BLEND_MODES = new BlockRenderLayer[5];

	/**
	 * Indigo currently support up to 3 sprite layers but is configured to recognize only one.
	 */
	public static final int MAX_SPRITE_DEPTH = 1;

	private static final int TEXTURE_DEPTH_MASK = 3;
	private static final int TEXTURE_DEPTH_SHIFT = 0;

	private static final int BLEND_MODE_MASK = 7;
	private static final int[] BLEND_MODE_SHIFT = new int[3];
	private static final int[] COLOR_DISABLE_FLAGS = new int[3];
	private static final int[] EMISSIVE_FLAGS = new int[3];
	private static final int[] DIFFUSE_FLAGS = new int[3];
	private static final int[] AO_FLAGS = new int[3];

	static {
		System.arraycopy(BlockRenderLayer.values(), 0, BLEND_MODES, 1, 4);

		int shift = Integer.bitCount(TEXTURE_DEPTH_MASK);

		for (int i = 0; i < 3; i++) {
			BLEND_MODE_SHIFT[i] = shift;
			shift += Integer.bitCount(BLEND_MODE_MASK);
			COLOR_DISABLE_FLAGS[i] = 1 << shift++;
			EMISSIVE_FLAGS[i] = 1 << shift++;
			DIFFUSE_FLAGS[i] = 1 << shift++;
			AO_FLAGS[i] = 1 << shift++;
		}
	}

	private static final ObjectArrayList<Value> LIST = new ObjectArrayList<>();
	private static final Int2ObjectOpenHashMap<Value> MAP = new Int2ObjectOpenHashMap<>();

	public static RenderMaterialImpl.Value byIndex(int index) {
		return LIST.get(index);
	}

	protected int bits;

	public BlockRenderLayer blendMode(int textureIndex) {
		return BLEND_MODES[(bits >> BLEND_MODE_SHIFT[textureIndex]) & BLEND_MODE_MASK];
	}

	public boolean disableColorIndex(int textureIndex) {
		return (bits & COLOR_DISABLE_FLAGS[textureIndex]) != 0;
	}

	public int spriteDepth() {
		return 1 + ((bits >> TEXTURE_DEPTH_SHIFT) & TEXTURE_DEPTH_MASK);
	}

	public boolean emissive(int textureIndex) {
		return (bits & EMISSIVE_FLAGS[textureIndex]) != 0;
	}

	public boolean disableDiffuse(int textureIndex) {
		return (bits & DIFFUSE_FLAGS[textureIndex]) != 0;
	}

	public boolean disableAo(int textureIndex) {
		return (bits & AO_FLAGS[textureIndex]) != 0;
	}

	public static class Value extends RenderMaterialImpl implements RenderMaterial {
		private final int index;

		/** True if any texture wants AO shading.  Simplifies check made by renderer at buffer-time. */
		public final boolean hasAo;

		/** True if any texture wants emissive lighting.  Simplifies check made by renderer at buffer-time. */
		public final boolean hasEmissive;

		private Value(int index, int bits) {
			this.index = index;
			this.bits = bits;
			hasAo = !disableAo(0)
					|| (spriteDepth() > 1 && !disableAo(1))
					|| (spriteDepth() == 3 && !disableAo(2));
			hasEmissive = emissive(0) || emissive(1) || emissive(2);
		}

		public int index() {
			return index;
		}
	}

	public static class Finder extends RenderMaterialImpl implements MaterialFinder {
		@Override
		public synchronized RenderMaterial find() {
			Value result = MAP.get(bits);

			if (result == null) {
				result = new Value(LIST.size(), bits);
				LIST.add(result);
				MAP.put(bits, result);
			}

			return result;
		}

		@Override
		public MaterialFinder clear() {
			bits = 0;
			return this;
		}

		@Override
		public MaterialFinder blendMode(int textureIndex, BlockRenderLayer blendMode) {
			final int shift = BLEND_MODE_SHIFT[textureIndex];
			// zero position is null (default) value
			final int ordinal = blendMode == null ? 0 : blendMode.ordinal() + 1;
			bits = (bits & ~(BLEND_MODE_MASK << shift)) | (ordinal << shift);
			return this;
		}

		@Override
		public MaterialFinder disableColorIndex(int textureIndex, boolean disable) {
			final int flag = COLOR_DISABLE_FLAGS[textureIndex];
			bits = disable ? (bits | flag) : (bits & ~flag);
			return this;
		}

		@Override
		public MaterialFinder spriteDepth(int depth) {
			if (depth < 1 || depth > MAX_SPRITE_DEPTH) {
				throw new IndexOutOfBoundsException("Invalid sprite depth: " + depth);
			}

			bits = (bits & ~(TEXTURE_DEPTH_MASK << TEXTURE_DEPTH_SHIFT)) | (--depth << TEXTURE_DEPTH_SHIFT);
			return this;
		}

		@Override
		public MaterialFinder emissive(int textureIndex, boolean isEmissive) {
			final int flag = EMISSIVE_FLAGS[textureIndex];
			bits = isEmissive ? (bits | flag) : (bits & ~flag);
			return this;
		}

		@Override
		public MaterialFinder disableDiffuse(int textureIndex, boolean disable) {
			final int flag = DIFFUSE_FLAGS[textureIndex];
			bits = disable ? (bits | flag) : (bits & ~flag);
			return this;
		}

		@Override
		public MaterialFinder disableAo(int textureIndex, boolean disable) {
			final int flag = AO_FLAGS[textureIndex];
			bits = disable ? (bits | flag) : (bits & ~flag);
			return this;
		}
	}
}
