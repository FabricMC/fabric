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
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;

/**
 * Interface for adding various tool attributes to items.
 */
public interface DynamicAttributeTool {
	Multimap<EntityAttribute, EntityAttributeModifier> EMPTY = ImmutableSetMultimap.of();

	/**
	 * Determines the mining level of the passed stack, which is used for calculating what blocks this tool is allowed to break.
	 *
	 * @param stack The item stack being used to mine the block
	 * @param user  The current user of the tool, or null if there isn't any
	 * @return The mining level of the item. 3 is equal to a diamond pick.
	 * @deprecated Use {@link #getMiningLevel(Tag, BlockState, ItemStack, LivingEntity)} to detect tag and block.
	 */
	@Deprecated
	default int getMiningLevel(ItemStack stack, @Nullable LivingEntity user) {
		return 0;
	}

	/**
	 * Determines the mining level of the passed stack, which is used for calculating what blocks this tool is allowed to break.
	 *
	 * @param tag   The tool tag the item stack is being compared to
	 * @param state The block to mine
	 * @param stack The item stack being used to mine the block
	 * @param user  The current user of the tool, or null if there isn't any
	 * @return The mining level of the item. 3 is equal to a diamond pick.
	 */
	default int getMiningLevel(Tag<Item> tag, BlockState state, ItemStack stack, @Nullable LivingEntity user) {
		return getMiningLevel(stack, user);
	}

	/**
	 * Determines the mining speed multiplier of the passed stack, which is one factor in overall mining speed.
	 *
	 * @param stack The item stack being used to mine the block
	 * @param user  The current user of the tool, or null if there isn't any
	 * @return The mining speed multiplier of the item. 8.0 is equal to a diamond pick.
	 * @deprecated Use {@link #getMiningSpeedMultiplier(Tag, BlockState, ItemStack, LivingEntity)} to detect tag and block.
	 */
	@Deprecated
	default float getMiningSpeedMultiplier(ItemStack stack, @Nullable LivingEntity user) {
		return 1.0F;
	}

	/**
	 * Determines the mining speed multiplier of the passed stack, which is one factor in overall mining speed.
	 *
	 * @param tag   The tool tag the item stack is being compared to
	 * @param state The block to mine
	 * @param stack The item stack being used to mine the block
	 * @param user  The current user of the tool, or null if there isn't any
	 * @return The mining speed multiplier of the item. 8.0 is equal to a diamond pick.
	 */
	default float getMiningSpeedMultiplier(Tag<Item> tag, BlockState state, ItemStack stack, @Nullable LivingEntity user) {
		return getMiningSpeedMultiplier(stack, user);
	}

	/**
	 * Post process the mining speed, this takes place after the mining speed has been calculated.
	 *
	 * <p>This allows bypassing the regular computation formula.
	 *
	 * @param tag          The tool tag the item stack is handled by
	 * @param state        The block to mine
	 * @param stack        The item stack being used to mine the block
	 * @param user         The current user of the tool, or null if there isn't any
	 * @param currentSpeed The mining speed before post process
	 * @param isEffective  whether the tool has been handled
	 * @return the speed after post processing
	 */
	default float postProcessMiningSpeed(Tag<Item> tag, BlockState state, ItemStack stack, @Nullable LivingEntity user, float currentSpeed, boolean isEffective) {
		return currentSpeed;
	}

	/**
	 * Add modifiers for any {@link net.minecraft.entity.attribute.EntityAttributes} your item should give when equipped, based on the stack.
	 *
	 * <p>Appends to either attribute modifier NBT or the result from {@link net.minecraft.item.Item#getAttributeModifiers(EquipmentSlot)}.</p>
	 *
	 * @param slot  The equipment slot this item is equipped in.
	 * @param stack The stack that's equipped.
	 * @param user  The current user of the tool, or none if there isn't any
	 * @return The dynamic modifiers to add on top of other modifiers on this stack. If none, return {@link #EMPTY}.
	 */
	default Multimap<EntityAttribute, EntityAttributeModifier> getDynamicModifiers(EquipmentSlot slot, ItemStack stack, @Nullable LivingEntity user) {
		return EMPTY;
	}
}
