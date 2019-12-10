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

package net.fabricmc.fabric.api.client.keybinding;

/**
 * Contains all of the known key categories present in vanilla minecraft.
 * These may be used without having to first register them.
 */
public final class KeyCategories {
    /**
     * Player movement controls.
     */
	public static final String MOVEMENT =    "key.categories.movement";
	/**
	 * Inventory and item movement and sorting.
	 */
	public static final String INVENTORY =   "key.categories.inventory";
	/**
	 * Creative mode controls (save/load toolbar, etc)
	 */
	public static final String CREATIVE =    "key.categories.creative";
	/**
	 * Attack and using items, activating blocks.
	 */
	public static final String GAMEPLAY =    "key.categories.gameplay";
	/**
	 * Multiplayer chat and commands keys.
	 */
	public static final String MULTIPLAYER = "key.categories.multiplayer";
	/**
	 * Miscellaneous, non-gameplay controls. Unused by mojang.
	 */
	public static final String UI =          "key.categories.ui";
	/**
	 * Anything else.
	 */
	public static final String MISC =        "key.categories.misc";

	private KeyCategories() {
	}
}
