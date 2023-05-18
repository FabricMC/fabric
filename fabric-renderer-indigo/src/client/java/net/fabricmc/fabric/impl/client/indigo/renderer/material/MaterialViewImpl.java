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

package net.fabricmc.fabric.impl.client.indigo.renderer.material;

import net.minecraft.util.math.MathHelper;

import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialView;
import net.fabricmc.fabric.api.util.TriState;

/**
 * Default implementation of the standard render materials.
 * The underlying representation is simply an int with bit-wise
 * packing of the various material properties. This offers
 * easy/fast interning via int/object hashmap.
 */
public class MaterialViewImpl implements MaterialView {
	private static final BlendMode[] BLEND_MODES = BlendMode.values();
	private static final int BLEND_MODE_COUNT = BLEND_MODES.length;
	private static final TriState[] TRI_STATES = TriState.values();
	private static final int TRI_STATE_COUNT = TRI_STATES.length;

	protected static final int BLEND_MODE_BIT_LENGTH = MathHelper.ceilLog2(BLEND_MODE_COUNT);
	protected static final int COLOR_DISABLE_BIT_LENGTH = 1;
	protected static final int EMISSIVE_BIT_LENGTH = 1;
	protected static final int DIFFUSE_BIT_LENGTH = 1;
	protected static final int AO_BIT_LENGTH = MathHelper.ceilLog2(TRI_STATE_COUNT);
	protected static final int GLINT_BIT_LENGTH = MathHelper.ceilLog2(TRI_STATE_COUNT);

	protected static final int BLEND_MODE_BIT_OFFSET = 0;
	protected static final int COLOR_DISABLE_BIT_OFFSET = BLEND_MODE_BIT_OFFSET + BLEND_MODE_BIT_LENGTH;
	protected static final int EMISSIVE_BIT_OFFSET = COLOR_DISABLE_BIT_OFFSET + COLOR_DISABLE_BIT_LENGTH;
	protected static final int DIFFUSE_BIT_OFFSET = EMISSIVE_BIT_OFFSET + EMISSIVE_BIT_LENGTH;
	protected static final int AO_BIT_OFFSET = DIFFUSE_BIT_OFFSET + DIFFUSE_BIT_LENGTH;
	protected static final int GLINT_BIT_OFFSET = AO_BIT_OFFSET + AO_BIT_LENGTH;
	protected static final int TOTAL_BIT_LENGTH = GLINT_BIT_OFFSET + GLINT_BIT_LENGTH;

	protected static final int BLEND_MODE_MASK = bitMask(BLEND_MODE_BIT_LENGTH, BLEND_MODE_BIT_OFFSET);
	protected static final int COLOR_DISABLE_FLAG = bitMask(COLOR_DISABLE_BIT_LENGTH, COLOR_DISABLE_BIT_OFFSET);
	protected static final int EMISSIVE_FLAG = bitMask(EMISSIVE_BIT_LENGTH, EMISSIVE_BIT_OFFSET);
	protected static final int DIFFUSE_FLAG = bitMask(DIFFUSE_BIT_LENGTH, DIFFUSE_BIT_OFFSET);
	protected static final int AO_MASK = bitMask(AO_BIT_LENGTH, AO_BIT_OFFSET);
	protected static final int GLINT_MASK = bitMask(GLINT_BIT_LENGTH, GLINT_BIT_OFFSET);

	protected static int bitMask(int bitLength, int bitOffset) {
		return ((1 << bitLength) - 1) << bitOffset;
	}

	protected static boolean areBitsValid(int bits) {
		int blendMode = (bits & BLEND_MODE_MASK) >> BLEND_MODE_BIT_OFFSET;
		int ao = (bits & AO_MASK) >> AO_BIT_OFFSET;
		int glint = (bits & GLINT_MASK) >> GLINT_BIT_OFFSET;

		return blendMode < BLEND_MODE_COUNT
				&& ao < TRI_STATE_COUNT
				&& glint < TRI_STATE_COUNT;
	}

	protected int bits;

	protected MaterialViewImpl(int bits) {
		this.bits = bits;
	}

	@Override
	public BlendMode blendMode() {
		return BLEND_MODES[(bits & BLEND_MODE_MASK) >> BLEND_MODE_BIT_OFFSET];
	}

	@Override
	public boolean disableColorIndex() {
		return (bits & COLOR_DISABLE_FLAG) != 0;
	}

	@Override
	public boolean emissive() {
		return (bits & EMISSIVE_FLAG) != 0;
	}

	@Override
	public boolean disableDiffuse() {
		return (bits & DIFFUSE_FLAG) != 0;
	}

	@Override
	public TriState ambientOcclusion() {
		return TRI_STATES[(bits & AO_MASK) >> AO_BIT_OFFSET];
	}

	@Override
	public TriState glint() {
		return TRI_STATES[(bits & GLINT_MASK) >> GLINT_BIT_OFFSET];
	}
}
