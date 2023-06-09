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

package net.fabricmc.fabric.test.model;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.resource.loader.v1.ResourceReloaderHolder;
import net.fabricmc.fabric.api.resource.loader.v1.ResourceReloaderKeys;
import net.fabricmc.fabric.api.resource.loader.v1.client.ClientResourceReloadEvents;

public class ModelTestModClient implements ClientModInitializer {
	public static final String ID = "fabric-models-v0-testmod";

	public static final Identifier MODEL_ID = new Identifier(ID, "half_red_sand");

	@Override
	public void onInitializeClient() {
		ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
			out.accept(MODEL_ID);
		});

		ClientResourceReloadEvents.REGISTER_RELOADERS.register(context -> {
			context.addReloader(SpecificModelReloadListener.ID, new SpecificModelReloadListener());
			context.addReloaderOrdering(ResourceReloaderKeys.BAKED_MODELS, SpecificModelReloadListener.ID);
			context.addReloaderOrdering(SpecificModelReloadListener.ID, ResourceReloaderKeys.ENTITY_RENDER_DISPATCHER);
		});

		LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
			if (entityRenderer instanceof PlayerEntityRenderer) {
				// Interface injection from other modules does not work, work around that
				ResourceReloaderHolder mcClient = (ResourceReloaderHolder) MinecraftClient.getInstance();
				// TODO: is this cleaner than storing a global instance? not sure...
				SpecificModelReloadListener specificModelListener = (SpecificModelReloadListener) mcClient.getResourceReloader(SpecificModelReloadListener.ID);
				registrationHelper.register(new BakedModelFeatureRenderer<>((PlayerEntityRenderer) entityRenderer, specificModelListener.getSpecificModel()));
			}
		});
	}
}
