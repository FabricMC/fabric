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

package net.fabricmc.fabric.test.model.loading;

import java.util.Arrays;
import java.util.Collection;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.profiler.Profiler;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;

public class SpecificModelReloadListener extends SinglePreparationResourceReloader<Unit> implements IdentifiableResourceReloadListener {
	public static final SpecificModelReloadListener INSTANCE = new SpecificModelReloadListener();
	public static final Identifier ID = new Identifier(ModelTestModClient.ID, "specific_model");

	private BakedModel specificModel;

	public BakedModel getSpecificModel() {
		return specificModel;
	}

	@Override
	protected Unit prepare(ResourceManager manager, Profiler profiler) {
		return Unit.INSTANCE;
	}

	@Override
	protected void apply(Unit loader, ResourceManager manager, Profiler profiler) {
		specificModel = MinecraftClient.getInstance().getBakedModelManager().getModel(ModelTestModClient.MODEL_ID);
	}

	@Override
	public Identifier getFabricId() {
		return ID;
	}

	@Override
	public Collection<Identifier> getFabricDependencies() {
		return Arrays.asList(ResourceReloadListenerKeys.MODELS);
	}
}
