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

import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.util.TriState;

public class RenderMaterialImpl extends MaterialViewImpl implements RenderMaterial {
	public static final int VALUE_COUNT = 1 << TOTAL_BIT_LENGTH;
	private static final RenderMaterialImpl[] BY_INDEX = new RenderMaterialImpl[VALUE_COUNT];

	static {
		for (int i = 0; i < VALUE_COUNT; i++) {
			BY_INDEX[i] = new RenderMaterialImpl(i);
		}
	}

	private final BlendMode blendMode;
	private final boolean disableColorIndex;
	private final boolean emissive;
	private final boolean disableDiffuse;
	private final TriState ambientOcclusion;

	private RenderMaterialImpl(int bits) {
		super(bits);

		blendMode = super.blendMode();
		disableColorIndex = super.disableColorIndex();
		emissive = super.emissive();
		disableDiffuse = super.disableDiffuse();
		ambientOcclusion = super.ambientOcclusion();
	}

	public int index() {
		return bits;
	}

	public static RenderMaterialImpl byIndex(int index) {
		return BY_INDEX[index];
	}

	public static RenderMaterialImpl setDisableDiffuse(RenderMaterialImpl material, boolean disable) {
		if (material.disableDiffuse() != disable) {
			return byIndex(disable ? (material.bits | DIFFUSE_FLAG) : (material.bits & ~DIFFUSE_FLAG));
		}

		return material;
	}

	@Override
	public BlendMode blendMode() {
		return blendMode;
	}

	@Override
	public boolean disableColorIndex() {
		return disableColorIndex;
	}

	@Override
	public boolean emissive() {
		return emissive;
	}

	@Override
	public boolean disableDiffuse() {
		return disableDiffuse;
	}

	@Override
	public TriState ambientOcclusion() {
		return ambientOcclusion;
	}
}
