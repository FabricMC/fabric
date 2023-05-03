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

import net.minecraft.util.math.MathHelper;

import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.util.TriState;

/**
 * Default implementation of the standard render materials.
 * The underlying representation is simply an int with bit-wise
 * packing of the various material properties. This offers
 * easy/fast interning via int/object hashmap.
 */
public abstract class RenderMaterialImpl {
	private static final BlendMode[] BLEND_MODES = BlendMode.values();
	private static final int BLEND_MODE_COUNT = BLEND_MODES.length;
	private static final TriState[] TRI_STATES = TriState.values();
	private static final int TRI_STATE_COUNT = TRI_STATES.length;

	protected static final int BLEND_MODE_BIT_LENGTH = MathHelper.ceilLog2(BLEND_MODE_COUNT);
	protected static final int COLOR_DISABLE_BIT_LENGTH = 1;
	protected static final int EMISSIVE_BIT_LENGTH = 1;
	protected static final int DIFFUSE_BIT_LENGTH = 1;
	protected static final int AO_BIT_LENGTH = MathHelper.ceilLog2(TRI_STATE_COUNT);

	protected static final int BLEND_MODE_BIT_OFFSET = 0;
	protected static final int COLOR_DISABLE_BIT_OFFSET = BLEND_MODE_BIT_OFFSET + BLEND_MODE_BIT_LENGTH;
	protected static final int EMISSIVE_BIT_OFFSET = COLOR_DISABLE_BIT_OFFSET + COLOR_DISABLE_BIT_LENGTH;
	protected static final int DIFFUSE_BIT_OFFSET = EMISSIVE_BIT_OFFSET + EMISSIVE_BIT_LENGTH;
	protected static final int AO_BIT_OFFSET = DIFFUSE_BIT_OFFSET + DIFFUSE_BIT_LENGTH;
	protected static final int TOTAL_BIT_LENGTH = AO_BIT_OFFSET + AO_BIT_LENGTH;

	protected static final int BLEND_MODE_MASK = bitMask(BLEND_MODE_BIT_LENGTH, BLEND_MODE_BIT_OFFSET);
	protected static final int COLOR_DISABLE_FLAG = bitMask(COLOR_DISABLE_BIT_LENGTH, COLOR_DISABLE_BIT_OFFSET);
	protected static final int EMISSIVE_FLAG = bitMask(EMISSIVE_BIT_LENGTH, EMISSIVE_BIT_OFFSET);
	protected static final int DIFFUSE_FLAG = bitMask(DIFFUSE_BIT_LENGTH, DIFFUSE_BIT_OFFSET);
	protected static final int AO_MASK = bitMask(AO_BIT_LENGTH, AO_BIT_OFFSET);

	public static final int VALUE_COUNT = 1 << TOTAL_BIT_LENGTH;

	protected static int bitMask(int bitLength, int bitOffset) {
		return ((1 << bitLength) - 1) << bitOffset;
	}

	private static final Value[] VALUES = new Value[VALUE_COUNT];

	static {
		for (int i = 0; i < VALUE_COUNT; i++) {
			VALUES[i] = new Value(i);
		}
	}

	public static RenderMaterialImpl.Value byIndex(int index) {
		return VALUES[index];
	}

	public static Value setDisableDiffuse(Value material, boolean disable) {
		if (material.disableDiffuse() != disable) {
			return byIndex(disable ? (material.bits | DIFFUSE_FLAG) : (material.bits & ~DIFFUSE_FLAG));
		}

		return material;
	}

	protected int bits;

	protected RenderMaterialImpl(int bits) {
		this.bits = bits;
	}

	public BlendMode blendMode() {
		int ordinal = (bits & BLEND_MODE_MASK) >> BLEND_MODE_BIT_OFFSET;

		if (ordinal >= BLEND_MODE_COUNT) {
			return BlendMode.DEFAULT;
		}

		return BLEND_MODES[ordinal];
	}

	public boolean disableColorIndex() {
		return (bits & COLOR_DISABLE_FLAG) != 0;
	}

	public boolean emissive() {
		return (bits & EMISSIVE_FLAG) != 0;
	}

	public boolean disableDiffuse() {
		return (bits & DIFFUSE_FLAG) != 0;
	}

	public TriState ambientOcclusion() {
		int ordinal = (bits & AO_MASK) >> AO_BIT_OFFSET;

		if (ordinal >= TRI_STATE_COUNT) {
			return TriState.DEFAULT;
		}

		return TRI_STATES[ordinal];
	}

	public static class Value extends RenderMaterialImpl implements RenderMaterial {
		private Value(int bits) {
			super(bits);
		}

		public int index() {
			return bits;
		}
	}

	public static class Finder extends RenderMaterialImpl implements MaterialFinder {
		private static int defaultBits = 0;

		static {
			Finder finder = new Finder();
			finder.ambientOcclusion(TriState.DEFAULT);
			defaultBits = finder.bits;
		}

		public Finder() {
			super(defaultBits);
		}

		@Override
		public MaterialFinder blendMode(BlendMode blendMode) {
			if (blendMode == null) {
				blendMode = BlendMode.DEFAULT;
			}

			bits = (bits & ~BLEND_MODE_MASK) | (blendMode.ordinal() << BLEND_MODE_BIT_OFFSET);
			return this;
		}

		@Override
		public MaterialFinder disableColorIndex(boolean disable) {
			bits = disable ? (bits | COLOR_DISABLE_FLAG) : (bits & ~COLOR_DISABLE_FLAG);
			return this;
		}

		@Override
		public MaterialFinder emissive(boolean isEmissive) {
			bits = isEmissive ? (bits | EMISSIVE_FLAG) : (bits & ~EMISSIVE_FLAG);
			return this;
		}

		@Override
		public MaterialFinder disableDiffuse(boolean disable) {
			bits = disable ? (bits | DIFFUSE_FLAG) : (bits & ~DIFFUSE_FLAG);
			return this;
		}

		@Override
		public MaterialFinder ambientOcclusion(TriState mode) {
			if (mode == null) {
				mode = TriState.DEFAULT;
			}

			bits = (bits & ~AO_MASK) | (mode.ordinal() << AO_BIT_OFFSET);
			return this;
		}

		@Override
		public MaterialFinder clear() {
			bits = defaultBits;
			return this;
		}

		@Override
		public RenderMaterial find() {
			return VALUES[bits];
		}
	}
}
