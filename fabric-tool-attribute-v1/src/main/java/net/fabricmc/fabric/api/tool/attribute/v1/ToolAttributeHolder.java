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

package net.fabricmc.fabric.api.tool.attribute.v1;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;

/**
 * Interface for adding various tool attributes to items.
 */
public interface ToolAttributeHolder {
	Multimap<String, EntityAttributeModifier> EMPTY = ImmutableSetMultimap.of();

	/**
	 * @param stack The stack to check on.
	 * @param user The current user of the tool, or none if there isn't any
	 * @return The mining level of the item. 3 is equal to a diamond pick.
	 */
	//TODO: nullable on user once we have an official @Nullable annotation in
	int getMiningLevel(ItemStack stack, LivingEntity user);

	/**
	 * @param stack The stack to check on.
	 * @param user The current user of the tool, or none if there isn't any
	 * @return The mining speed of the item. 8.0 is equal to a diamond pick.
	 */
	//TODO: nullable on user once we have an official @Nullable annotation in
	float getMiningSpeed(ItemStack stack, LivingEntity user);

	/**
	 * Add modifiers for any {@link net.minecraft.entity.attribute.EntityAttributes} your item should give when equipped, based on the stack.
	 *
	 * <p>Appends to either attribute modifier NBT or the result from {@link net.minecraft.item.Item#getModifiers(EquipmentSlot)}.</p>
	 *
	 * @param slot  The equipment slot this item is equipped in.
	 * @param stack The stack that's equipped.
	 * @param user The current user of the tool, or none if there isn't any
	 * @return The dynamic modifiers to add on top of other modifiers on this stack. If none, return {@link #EMPTY}.
	 */
	//TODO: nullable on user once we have an official @Nullable annotation in
	default Multimap<String, EntityAttributeModifier> getDynamicModifiers(EquipmentSlot slot, ItemStack stack, LivingEntity user) {
		return EMPTY;
	}
}
