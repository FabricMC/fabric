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

package net.fabricmc.fabric.api.tag.v1;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.tag.TagKey;

import net.fabricmc.fabric.impl.tag.common.TagRegistration;

public class CommonEnchantmentTags {
	/**
	 * A tag containing enchantments that increase the amount or
	 * quality of drops from blocks, such as {@link net.minecraft.enchantment.Enchantments#FORTUNE}.
	 */
	public static final TagKey<Enchantment> INCREASES_BLOCK_DROPS = register("fortune");
	/**
	 * A tag containing enchantments that increase the amount or
	 * quality of drops from entities, such as {@link net.minecraft.enchantment.Enchantments#LOOTING}.
	 */
	public static final TagKey<Enchantment> INCREASES_ENTITY_DROPS = register("looting");
	/**
	 * A tag containing enchantments that cause a block to drop itself in item form
	 * rather than some other item, such as {@link net.minecraft.enchantment.Enchantments#SILK_TOUCH}.
	 */
	public static final TagKey<Enchantment> IDENTICAL_DROPS = register("identical_drops");
	/**
	 * For enchantments that increase the damage dealt by an item.
	 */
	public static final TagKey<Enchantment> WEAPON_DAMAGE_ENHANCEMENT = register("weapon_damage_enhancement");

	/**
	 * For enchantments that increase movement speed or otherwise benefit the entity wearing armor enchanted with it.
	 */
	public static final TagKey<Enchantment> ENTITY_MOVEMENT_ENHANCEMENT = register("entity_movement_enhancement");

	private static TagKey<Enchantment> register(String tagID) {
		return TagRegistration.ENCHANTMENT_TAG_REGISTRATION.registerCommon(tagID);
	}
}
