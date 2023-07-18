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

package net.fabricmc.fabric.mixin.client.model.loading;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.profiler.Profiler;

import net.fabricmc.fabric.api.client.model.loading.v1.FabricBakedModelManager;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.impl.client.model.loading.ModelLoadingPluginManager;

@Mixin(BakedModelManager.class)
public class BakedModelManagerMixin implements FabricBakedModelManager {
	@Shadow
	private Map<Identifier, BakedModel> models;

	@Override
	public BakedModel getModel(Identifier id) {
		return models.get(id);
	}

	@Redirect(
			method = "reload",
			at = @At(
					value = "INVOKE",
					target = "java/util/concurrent/CompletableFuture.thenCombineAsync(Ljava/util/concurrent/CompletionStage;Ljava/util/function/BiFunction;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;",
					remap = false
			),
			allow = 1)
	private CompletableFuture<ModelLoader> loadModelPluginData(
			CompletableFuture<Map<Identifier, JsonUnbakedModel>> self,
			CompletionStage<Map<Identifier, List<ModelLoader.SourceTrackedData>>> otherFuture,
			BiFunction<Map<Identifier, JsonUnbakedModel>, Map<Identifier, List<ModelLoader.SourceTrackedData>>, ModelLoader> modelLoaderConstructor,
			Executor executor,
			// reload args
			ResourceReloader.Synchronizer synchronizer,
			ResourceManager manager,
			Profiler prepareProfiler,
			Profiler applyProfiler,
			Executor prepareExecutor,
			Executor applyExecutor) {
		CompletableFuture<List<ModelLoadingPlugin>> pluginsFuture = ModelLoadingPluginManager.preparePlugins(manager, prepareExecutor);
		CompletableFuture<Pair<Map<Identifier, JsonUnbakedModel>, Map<Identifier, List<ModelLoader.SourceTrackedData>>>> pairFuture = self.thenCombine(otherFuture, Pair::new);
		return pairFuture.thenCombineAsync(pluginsFuture, (pair, plugins) -> {
			ModelLoadingPluginManager.CURRENT_PLUGINS.set(plugins);
			return modelLoaderConstructor.apply(pair.getLeft(), pair.getRight());
		}, executor);
	}
}
