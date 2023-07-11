package net.fabricmc.fabric.impl.client.model.loading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Util;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;

public final class ModelLoadingPluginManager {
	private static final List<ModelLoadingPlugin> PLUGINS = new ArrayList<>();
	private static final List<PreparablePluginHolder<?>> PREPARABLE_PLUGINS = new ArrayList<>();

	public static final ThreadLocal<List<ModelLoadingPlugin>> CURRENT_PLUGINS = new ThreadLocal<>();

	public static void registerPlugin(ModelLoadingPlugin plugin) {
		PLUGINS.add(plugin);
	}

	public static <T> void registerPlugin(PreparableModelLoadingPlugin.DataLoader<T> loader, PreparableModelLoadingPlugin<T> plugin) {
		PREPARABLE_PLUGINS.add(new PreparablePluginHolder<>(loader, plugin));
	}

	/**
	 * The current exception behavior as of 1.20 is as follows.
	 * If getting a {@link CompletableFuture}s throws then the whole client will crash.
	 * If a {@link CompletableFuture} completes exceptionally then the resource reload will fail.
	 */
	public static CompletableFuture<List<ModelLoadingPlugin>> preparePlugins(ResourceManager resourceManager, Executor executor) {
		List<CompletableFuture<ModelLoadingPlugin>> futures = new ArrayList<>();

		for (ModelLoadingPlugin plugin : PLUGINS) {
			futures.add(CompletableFuture.completedFuture(plugin));
		}

		for (PreparablePluginHolder<?> holder : PREPARABLE_PLUGINS) {
			futures.add(preparePlugin(holder, resourceManager, executor));
		}

		return Util.combine(futures);
	}

	private static <T> CompletableFuture<ModelLoadingPlugin> preparePlugin(PreparablePluginHolder<T> holder, ResourceManager resourceManager, Executor executor) {
		CompletableFuture<T> dataFuture = holder.loader.load(resourceManager, executor);
		return dataFuture.thenApply(data -> pluginContext -> holder.plugin.onInitializeModelLoader(data, pluginContext));
	}

	private ModelLoadingPluginManager() { }

	private record PreparablePluginHolder<T>(PreparableModelLoadingPlugin.DataLoader<T> loader, PreparableModelLoadingPlugin<T> plugin) { }
}
