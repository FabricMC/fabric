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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.options.KeyBinding;

import net.fabricmc.fabric.mixin.client.keybinding.KeyBindingAccessor;

public final class KeyBindingRegistryImpl {
	public static final KeyBindingRegistryImpl INSTANCE = new KeyBindingRegistryImpl();
	private static final Logger LOGGER = LogManager.getLogger();

	private final List<KeyBinding> fabricKeyBindings = Lists.newArrayList();

	private KeyBindingRegistryImpl() {
	}

	private Map<String, Integer> getCategoryMap() {
		return KeyBindingAccessor.fabric_getCategoryMap();
	}

	private boolean hasCategory(String categoryTranslationKey) {
		return getCategoryMap().containsKey(categoryTranslationKey);
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

	public boolean registerKeyBinding(KeyBinding binding) {
		for (KeyBinding existingKeyBindings : fabricKeyBindings) {
			if (existingKeyBindings == binding) {
				return false;
			} else if (existingKeyBindings.getId().equals(binding.getId())) {
				throw new RuntimeException("Attempted to register two key bindings with equal ID: " + binding.getId() + "!");
			}
		}

		if (!hasCategory(binding.getCategory())) {
			addCategory(binding.getCategory());
		}

		return fabricKeyBindings.add(binding);
	}

	public KeyBinding[] process(KeyBinding[] keysAll) {
		List<KeyBinding> newKeysAll = Lists.newArrayList(keysAll);
		newKeysAll.removeAll(fabricKeyBindings);
		newKeysAll.addAll(fabricKeyBindings);
		return newKeysAll.toArray(new KeyBinding[0]);
	}
}
