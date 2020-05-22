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

package net.fabricmc.fabric.impl.client.keybinding;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.options.KeyBinding;

import net.fabricmc.fabric.api.client.keybinding.v1.ModdedKeyBinding;

public class KeyBindingRegistry {
	public static final KeyBindingRegistry INSTANCE = new KeyBindingRegistry();
	private static final Logger LOGGER = LogManager.getLogger();

	private Map<String, Integer> cachedCategoryMap;
	private final List<ModdedKeyBinding> moddedKeyBindings = Lists.newArrayList();

	private KeyBindingRegistry() {
	}

	private Map<String, Integer> getCategoryMap() {
		if (cachedCategoryMap == null) {
			try {
				//noinspection JavaReflectionMemberAccess
				Method m = KeyBinding.class.getDeclaredMethod("fabric_getCategoryMap");
				m.setAccessible(true);

				//noinspection unchecked
				cachedCategoryMap = (Map<String, Integer>) m.invoke(null);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			if (cachedCategoryMap == null) {
				throw new RuntimeException("Cached key binding category map missing!");
			}
		}

		return cachedCategoryMap;
	}

	private boolean hasCategory(String categoryName) {
		return getCategoryMap().containsKey(categoryName);
	}

	public boolean addCategory(String categoryTranslationKey) {
		Map<String, Integer> map = getCategoryMap();

		if (map.containsKey(categoryTranslationKey)) {
			return false;
		}

		Optional<Integer> largest = map.values().stream().max(Integer::compareTo);
		int largestInt = largest.orElse(0);
		map.put(categoryTranslationKey, largestInt + 1);
		return true;
	}

	public boolean registerKeyBinding(ModdedKeyBinding binding) {
		for (KeyBinding exBinding : moddedKeyBindings) {
			if (exBinding == binding) {
				return false;
			} else if (exBinding.getId().equals(binding.getId())) {
				throw new RuntimeException("Attempted to register two key bindings with equal ID: " + binding.getId() + "!");
			}
		}

		if (!hasCategory(binding.getCategory())) {
			addCategory(binding.getCategory());
		}

		moddedKeyBindings.add(binding);
		return true;
	}

	public KeyBinding[] process(KeyBinding[] keysAll) {
		List<KeyBinding> newKeysAll = Stream.of(keysAll).filter(keyBinding -> !(keyBinding instanceof ModdedKeyBinding)).collect(Collectors.toList());
		newKeysAll.addAll(moddedKeyBindings);
		return newKeysAll.toArray(new KeyBinding[0]);
	}
}
