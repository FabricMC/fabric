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

package net.fabricmc.fabric.test.renderer.simple.client;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;

import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelVariantProvider;
import net.fabricmc.fabric.test.renderer.simple.RendererTest;

public class PillarModelVariantProvider implements ModelVariantProvider {
	@Override
	@Nullable
	public UnbakedModel loadModelVariant(ModelIdentifier modelId, ModelProviderContext context) {
		if (RendererTest.PILLAR_ID.equals(modelId)) {
			return new PillarUnbakedModel();
		} else {
			return null;
		}
	}
}
