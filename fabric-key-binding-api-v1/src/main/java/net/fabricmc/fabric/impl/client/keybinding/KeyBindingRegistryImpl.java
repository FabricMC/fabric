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

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;

import net.fabricmc.fabric.mixin.client.keybinding.GameOptionsAccessor;
import net.fabricmc.fabric.mixin.client.keybinding.KeyBindingAccessor;

public final class KeyBindingRegistryImpl {
	public static final ReferenceArrayList<KeyBinding> unregisteredKeyBindings = ReferenceArrayList.wrap(new KeyBinding[40], 0);

	private static final ReferenceArrayList<KeyBinding> moddedKeyBindings = new ReferenceArrayList<>();
	private static ReferenceArrayList<KeyBinding> originalKeyBindings;
	private static GameOptionsAccessor options;

	private KeyBindingRegistryImpl() {
	}

	private static Map<String, Integer> getCategoryMap() {
		return KeyBindingAccessor.fabric_getCategoryMap();
	}

	private static boolean hasCategory(String categoryTranslationKey) {
		return getCategoryMap().containsKey(categoryTranslationKey);
	}

	public static boolean addCategory(String categoryTranslationKey) {
		Map<String, Integer> map = getCategoryMap();

		if (map.containsKey(categoryTranslationKey)) {
			return false;
		}

		Optional<Integer> largest = map.values().stream().max(Integer::compareTo);
		int largestInt = largest.orElse(0);
		map.put(categoryTranslationKey, largestInt + 1);
		return true;
	}

	public static void removeCategory(String categoryTranslationKey) {
		KeyBindingAccessor.fabric_getKeyCategories().remove(categoryTranslationKey);

		Integer order = getCategoryMap().remove(categoryTranslationKey);

		if (order != null) {
			for (Map.Entry<String, Integer> entry : getCategoryMap().entrySet()) {
				Integer value = entry.getValue();

				if (value > order) {
					entry.setValue(value - 1);
				}
			}
		}
	}

	public static KeyBinding registerKeyBinding(KeyBinding keyBinding) {
		for (KeyBinding existingKeyBinding : moddedKeyBindings) {
			if (existingKeyBinding == keyBinding) {
				throw new IllegalArgumentException("Attempted to register the same key binding twice.");
			} else if (existingKeyBinding.getTranslationKey().equals(keyBinding.getTranslationKey())) {
				throw new RuntimeException("Attempted to register two key bindings with equal ID: " + keyBinding.getTranslationKey() + "!");
			}
		}

		if (!hasCategory(keyBinding.getCategory())) {
			addCategory(keyBinding.getCategory());
		}

		moddedKeyBindings.add(keyBinding);
		unregisteredKeyBindings.remove(keyBinding);

		if (options != null) {
			KeyBindingAccessor.fabric_getKeysToBindings().put(((KeyBindingAccessor) keyBinding).fabric_getBoundKey(), keyBinding);
			KeyBindingAccessor.fabric_getKeysById().put(keyBinding.getTranslationKey(), keyBinding);

			options.setKeyBindings(process());
		}

		return keyBinding;
	}

	public static boolean unregisterKeyBinding(KeyBinding keyBinding) {
		if (removeKeyBinding(keyBinding)) {
			unregisteredKeyBindings.add(keyBinding);
			((GameOptionsAccessor) MinecraftClient.getInstance().options).setKeyBindings(process());

			for (KeyBinding other : moddedKeyBindings) {
				if (Objects.equals(other.getCategory(), keyBinding.getCategory())) {
					return true;
				}
			}

			removeCategory(keyBinding.getCategory());

			return true;
		}

		return false;
	}

	public static boolean isRegistered(KeyBinding keyBinding) {
		return moddedKeyBindings.contains(keyBinding);
	}

	private static boolean removeKeyBinding(KeyBinding keyBinding) {
		KeyBindingAccessor.fabric_getKeysById().remove(keyBinding.getTranslationKey());
		KeyBindingAccessor.fabric_getKeysToBindings().remove(((KeyBindingAccessor) keyBinding).fabric_getBoundKey());

		return moddedKeyBindings.remove(keyBinding);
	}

	/**
	 * Processes the key bindings array for our modded ones.
	 */
	public static KeyBinding[] process() {
		ReferenceArrayList<KeyBinding> newKeysAll = originalKeyBindings.clone();
		newKeysAll.addAll(moddedKeyBindings);
		return newKeysAll.toArray(new KeyBinding[0]);
	}

	public static void init(GameOptionsAccessor options, KeyBinding[] keysAll) {
		if (KeyBindingRegistryImpl.options == null) {
			KeyBindingRegistryImpl.options = options;
			originalKeyBindings = ReferenceArrayList.wrap(keysAll);
		}
	}
}
