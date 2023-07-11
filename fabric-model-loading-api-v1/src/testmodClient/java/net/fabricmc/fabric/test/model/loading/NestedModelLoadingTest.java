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

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;

/**
 * Tests that deep model resolution resolve each model a single time, depth-first.
 */
public class NestedModelLoadingTest implements ClientModInitializer {
	private static final Logger LOGGER = LogUtils.getLogger();

	private static Identifier id(String path) {
		return new Identifier("fabric-model-loading-api-v1-testmod", path);
	}

	private static final Identifier BASE_MODEL = id("nested_base");
	private static final Identifier NESTED_MODEL_1 = id("nested_1");
	private static final Identifier NESTED_MODEL_2 = id("nested_2");
	private static final Identifier NESTED_MODEL_3 = id("nested_3");
	private static final Identifier NESTED_MODEL_4 = id("nested_4");
	private static final Identifier NESTED_MODEL_5 = id("nested_5");
	private static final Identifier TARGET_MODEL = new Identifier("minecraft", "block/stone");

	@Override
	public void onInitializeClient() {
		ModelLoadingPlugin.register(pluginContext -> {
			pluginContext.addModels(BASE_MODEL);

			pluginContext.resolveModel().register(context -> {
				Identifier id = context.id();
				UnbakedModel ret = null;

				if (id.equals(BASE_MODEL)) {
					LOGGER.info("Nested model 1 started loading");
					ret = context.getOrLoadModel(NESTED_MODEL_1);
					LOGGER.info("Nested model 1 finished loading");
				} else if (id.equals(NESTED_MODEL_1)) {
					LOGGER.info(" Nested model 2 started loading");
					ret = context.getOrLoadModel(NESTED_MODEL_2);
					LOGGER.info(" Nested model 2 finished loading");
				} else if (id.equals(NESTED_MODEL_2)) {
					LOGGER.info("  Nested model 3 started loading");
					ret = context.getOrLoadModel(NESTED_MODEL_3);
					LOGGER.info("  Nested model 3 finished loading");
				} else if (id.equals(NESTED_MODEL_3)) {
					// Will be overridden by the model modifier below anyway.
					LOGGER.info("   Returning dummy model for nested model 3");
					ret = context.getOrLoadModel(ModelLoader.MISSING_ID);
				} else if (id.equals(NESTED_MODEL_4)) {
					// Will be overridden by the model modifier below anyway.
					LOGGER.info("    Returning dummy model for nested model 4");
					ret = context.getOrLoadModel(ModelLoader.MISSING_ID);
				} else if (id.equals(NESTED_MODEL_5)) {
					LOGGER.info("     Target model started loading");
					ret = context.getOrLoadModel(TARGET_MODEL);
					LOGGER.info("     Target model finished loading");
				}

				return ret;
			});

			pluginContext.modifyModelOnLoad().register((model, context) -> {
				UnbakedModel ret = model;

				if (context.id().equals(NESTED_MODEL_3)) {
					Identifier id = context.id();

					LOGGER.info("   Nested model 4 started loading");
					ret = context.getOrLoadModel(NESTED_MODEL_4);
					LOGGER.info("   Nested model 4 finished loading");

					if (!id.equals(context.id())) {
						throw new AssertionError("Context object should not have changed.");
					}
				} else if (context.id().equals(NESTED_MODEL_4)) {
					LOGGER.info("    Nested model 5 started loading");
					ret = context.getOrLoadModel(NESTED_MODEL_5);
					LOGGER.info("    Nested model 5 finished loading");
				}

				return ret;
			});
		});
	}
}
