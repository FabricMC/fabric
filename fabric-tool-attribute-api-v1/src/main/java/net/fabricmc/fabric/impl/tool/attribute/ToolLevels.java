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

package net.fabricmc.fabric.impl.tool.attribute;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.tool.attribute.v1.ToolLevel;

public final class ToolLevels implements ModInitializer {
	private static final ToolLevel NONE = () -> -1.0F;
	private static final Map<Identifier, Float> VOTE_DATA = new HashMap<>();
	private static final Map<Identifier, VotedToolLevel> VOTE_CACHE = new HashMap<>();

	public static ToolLevel of(float level) {
		if (level < 0) {
			return NONE;
		}

		return () -> level;
	}

	public static VotedToolLevel by(Identifier id, ToolLevel fallback) {
		return VOTE_CACHE.computeIfAbsent(id, identifier -> new VotedToolLevel(identifier, fallback));
	}

	@Override
	public void onInitialize() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new VotedToolLevelLoader());
	}

	private static final class VotedToolLevel implements ToolLevel.Identified {
		private final Identifier identifier;
		@NotNull
		private final ToolLevel fallback;
		@Nullable
		private Float value = null;
		private boolean dirty = true;

		VotedToolLevel(Identifier identifier, @NotNull ToolLevel fallback) {
			this.identifier = identifier;
			this.fallback = fallback;
		}

		@Override
		public float getLevel() {
			if (dirty) {
				dirty = false;
				value = VOTE_DATA.get(identifier);
			}
			
			return value == null ? fallback.getLevel() : value;
		}

		@Override
		public Identifier getId() {
			return identifier;
		}

		public void markDirty() {
			this.dirty = true;
			this.value = null;
		}
	}

	private static final class VotedToolLevelLoader implements IdentifiableResourceReloadListener {
		private static final Logger LOGGER = LogManager.getLogger();
		private static final Identifier ID = new Identifier("fabric:private/tool_level_vote_loader");
		private static final int JSON_EXTENSION_LENGTH = ".json".length();
		private static final String RESOURCE_TYPE = "fabric_tool_levels";
		private static final Gson GSON = new Gson();

		@Override
		public Identifier getFabricId() {
			return ID;
		}

		@Override
		public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
			return CompletableFuture.supplyAsync(() -> {
				Map<Identifier, FloatList> map = Maps.newHashMap();

				for (Identifier resourceId : manager.findResources(RESOURCE_TYPE, path -> path.endsWith(".json"))) {
					String resourcePath = resourceId.getPath();
					Identifier id = new Identifier(resourceId.getNamespace(), resourcePath.substring(RESOURCE_TYPE.length() + 1, resourcePath.length() - JSON_EXTENSION_LENGTH));

					try {
						for (Resource resource : manager.getAllResources(resourceId)) {
							try (Reader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
								JsonObject json = JsonHelper.deserialize(GSON, reader, JsonObject.class);

								if (json == null) {
									LOGGER.error("Couldn't load tool level list {} from {} in data pack {} as it is empty or null", id, resourceId, resource.getResourcePackName());
								} else {
									FloatList levels = map.computeIfAbsent(id, identifier -> new FloatArrayList());

									if (JsonHelper.getBoolean(json, "replace", false)) {
										levels.clear();
									}

									float value = JsonHelper.getFloat(json, "value");

									if (value < 0) {
										value = -1.0F;
									}

									levels.add(value);
								}
							} catch (RuntimeException | IOException exception) {
								LOGGER.error("Couldn't read tool level list {} from {} in data pack {}", id, resourceId, resource.getResourcePackName(), exception);
							}

							IOUtils.closeQuietly(resource);
						}
					} catch (IOException exception) {
						LOGGER.error("Couldn't read tool level list {} from {}", id, resourceId, exception);
					}
				}

				return map;
			}, prepareExecutor).thenCompose(synchronizer::whenPrepared).thenAcceptAsync(map -> {
				VOTE_DATA.clear();

				for (VotedToolLevel level : VOTE_CACHE.values()) {
					level.markDirty();
				}

				for (Map.Entry<Identifier, FloatList> entry : map.entrySet()) {
					FloatList floats = entry.getValue();
					Object2IntMap<Float> frequency = new Object2IntOpenHashMap<>();

					for (Float f : floats) {
						frequency.put(f, frequency.getOrDefault(f, 0) + 1);
					}

					VOTE_DATA.put(entry.getKey(), frequency.object2IntEntrySet().stream()
							.max(Comparator.<Object2IntMap.Entry<Float>>comparingInt(Object2IntMap.Entry::getIntValue)
									.thenComparing(Map.Entry::getKey, Comparator.comparing(floats::lastIndexOf))
							)
							.map(Object2IntMap.Entry::getKey)
							.orElse(-1.0F));
				}
			}, applyExecutor);
		}
	}
}
