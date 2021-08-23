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

package net.fabricmc.fabric.impl.tag.extension;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.Main;
import net.minecraft.tag.RequiredTagList;
import net.minecraft.tag.RequiredTagListRegistry;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.tag.TagGroupLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import net.fabricmc.fabric.api.tag.TagFactory;
import net.fabricmc.fabric.mixin.tag.extension.DynamicRegistryManagerAccessor;

@SuppressWarnings("ClassCanBeRecord")
public final class TagFactoryImpl<T> implements TagFactory<T> {
	private static final Logger LOGGER = LogManager.getLogger();

	public static final Map<RegistryKey<? extends Registry<?>>, RequiredTagList<?>> TAG_LISTS = new HashMap<>();

	public static <T> TagFactory<T> of(Supplier<TagGroup<T>> tagGroupSupplier) {
		return new TagFactoryImpl<>(tagGroupSupplier);
	}

	@SuppressWarnings("unchecked")
	public static <T> TagFactory<T> of(RegistryKey<? extends Registry<T>> registryKey, String dataType) {
		RequiredTagList<T> tagList;

		// Use already registered tag list for the registry if it has the same dataType, in case multiple mods tried to do it.
		if (TAG_LISTS.containsKey(registryKey)) {
			tagList = (RequiredTagList<T>) TAG_LISTS.get(registryKey);
			// Throw an exception if the tagList has different dataType.
			Preconditions.checkArgument(tagList.getDataType().equals(dataType), "Tag list for registry %s is already existed with data type %s", registryKey.getValue(), tagList.getDataType());
		} else {
			tagList = RequiredTagListRegistry.register(registryKey, dataType);
			TAG_LISTS.put(registryKey, tagList);
		}

		return of(tagList::getGroup);
	}

	/**
	 * Manually load tags for dynamic registries and add the resulting tag group to the tag list.
	 *
	 * <p>Minecraft loads the resource manager before dynamic registries, making tags for them fail to load
	 * if it mentions datapack entries. The solution is to manually load tags after the registry is loaded.
	 *
	 * <p>Look at server's {@link Main#main} function calls for {@link ServerResourceManager#reload} and
	 * {@link RegistryOps#method_36574} for the relevant code.
	 */
	public static void loadDynamicRegistryTags(DynamicRegistryManager registryManager, ResourceManager resourceManager) {
		Stopwatch stopwatch = Stopwatch.createStarted();
		int loadedTags = 0;

		for (RequiredTagList<?> tagList : TAG_LISTS.values()) {
			if (isDynamic(tagList)) {
				RegistryKey<? extends Registry<?>> registryKey = tagList.getRegistryKey();
				Registry<?> registry = registryManager.get(registryKey);
				TagGroupLoader<?> tagGroupLoader = new TagGroupLoader<>(registry::getOrEmpty, tagList.getDataType());
				TagGroup<?> tagGroup = tagGroupLoader.load(resourceManager);
				((FabricTagManagerHooks) ServerTagManagerHolder.getTagManager()).fabric_addTagGroup(registryKey, tagGroup);
				tagList.updateTagManager(ServerTagManagerHolder.getTagManager());
				loadedTags += tagGroup.getTags().size();
			}
		}

		if (loadedTags > 0) {
			LOGGER.info("Loaded {} dynamic registry tags in {}", loadedTags, stopwatch);
		}
	}

	public static boolean isDynamic(RequiredTagList<?> tagList) {
		return DynamicRegistryManagerAccessor.getInfos().containsKey(tagList.getRegistryKey());
	}

	private final Supplier<TagGroup<T>> tagGroupSupplier;

	private TagFactoryImpl(Supplier<TagGroup<T>> tagGroupSupplier) {
		this.tagGroupSupplier = tagGroupSupplier;
	}

	@Override
	public Tag.Identified<T> create(Identifier id) {
		return new TagDelegate<>(id, tagGroupSupplier);
	}
}
