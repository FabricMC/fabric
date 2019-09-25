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

import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.mixin.client.keybinding.KeyBindingAccess;
import net.minecraft.client.options.KeyBinding;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class KeyBindingRegistryImpl implements KeyBindingRegistry {
	public static final KeyBindingRegistryImpl INSTANCE = new KeyBindingRegistryImpl();
	private static final Logger LOGGER = LogManager.getLogger();

	private final Map<String, Integer> categoryMap = KeyBindingAccess.getCategoryOrderMap();
	private final List<FabricKeyBinding> fabricKeyBindingList;

	private KeyBindingRegistryImpl() {
		fabricKeyBindingList = new ArrayList<>();
	}

	private boolean hasCategory(String categoryName) {
		return categoryMap.containsKey(categoryName);
	}

	@Override
	public boolean addCategory(String categoryName) {
		if (categoryMap.containsKey(categoryName)) {
			return false;
		}

		Optional<Integer> largest = categoryMap.values().stream().max(Integer::compareTo);
		int largestInt = largest.orElse(0);
		categoryMap.put(categoryName, largestInt + 1);
		return true;
	}

	@Override
	public boolean register(FabricKeyBinding binding) {
		for (KeyBinding exBinding : fabricKeyBindingList) {
			if (exBinding == binding) {
				return false;
			} else if (exBinding.getId().equals(binding.getId())) {
				throw new RuntimeException("Attempted to register two key bindings with equal ID: " + binding.getId() + "!");
			}
		}

		if (!hasCategory(binding.getCategory())) {
			LOGGER.warn("Tried to register key binding with unregistered category '" + binding.getCategory() + "' - please use addCategory to ensure intended category ordering!");
			addCategory(binding.getCategory());
		}

		fabricKeyBindingList.add(binding);
		return true;
	}

	public KeyBinding[] process(KeyBinding[] keysAll) {
		List<KeyBinding> newKeysAll = new ArrayList<>();
		for (KeyBinding binding : keysAll) {
			if (!(binding instanceof FabricKeyBinding)) {
				newKeysAll.add(binding);
			}
		}

		newKeysAll.addAll(fabricKeyBindingList);
		return newKeysAll.toArray(new KeyBinding[0]);
	}
}
