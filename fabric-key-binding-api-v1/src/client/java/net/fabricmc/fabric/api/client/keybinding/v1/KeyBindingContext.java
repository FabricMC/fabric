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

package net.fabricmc.fabric.api.client.keybinding.v1;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

import net.fabricmc.fabric.impl.client.keybinding.KeyBindingExtensions;

/**
 * {@link KeyBindingContext} decides how {@link KeyBinding} with same bounded key behaves in regard to each other.
 *
 * <p>Bindings with different context will not conflict with each other even if they have the same bounded key.
 */
public interface KeyBindingContext {
	/**
	 * {@link KeyBinding} that used in-game. All vanilla key binds have this context.
	 */
	KeyBindingContext IN_GAME = () -> MinecraftClient.getInstance().currentScreen == null;

	/**
	 * {@link KeyBinding} that used when a screen is open.
	 */
	KeyBindingContext IN_SCREEN = () -> MinecraftClient.getInstance().currentScreen != null;

	/**
	 * {@link KeyBinding} that might be used in any context. This context conflicts with any other context.
	 */
	KeyBindingContext ALL = new KeyBindingContext() {
		@Override
		public boolean isActive() {
			return true;
		}

		@Override
		public boolean conflictsWith(KeyBindingContext other) {
			return true;
		}
	};

	static KeyBindingContext of(KeyBinding binding) {
		return ((KeyBindingExtensions) binding).fabric_getContext();
	}

	static boolean conflicts(KeyBindingContext left, KeyBindingContext right) {
		return left.conflictsWith(right) || right.conflictsWith(left);
	}

	boolean isActive();

	/**
	 * Use {@link #conflicts(KeyBindingContext, KeyBindingContext)} for checking if two context conflicts.
	 */
	@ApiStatus.OverrideOnly
	default boolean conflictsWith(KeyBindingContext other) {
		return this == other;
	}
}
