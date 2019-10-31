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

package net.fabricmc.fabric.api.tools.v1;

import com.google.common.collect.Multimap;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;

/**
 * A tool whose properties can vary based on its user.
 */
public interface ActableAttributeHolder extends ToolAttributeHolder {
	/**
	 * @param stack The stack being used.
	 * @param actor The actor attempting to mine.
	 * @return The mining level for this stack and actor.
	 */
	default int getMiningLevel(ItemStack stack, ToolActor<?> actor) {
		return getMiningLevel(stack);
	}

	/**
	 * @param stack The stack being used.
	 * @param actor The actor attempting to mine.
	 * @return The mining speed for this stack and actor.
	 */
	default float getMiningSpeed(ItemStack stack, ToolActor<?> actor) {
		return getMiningSpeed(stack);
	}

	/**
	 * Appended to {@link ToolAttributeHolder#getDynamicModifiers(EquipmentSlot, ItemStack)}. Do not call to it unless you want doubling.
	 *
	 * @param slot  The equipment slot this item is equipped in.
	 * @param stack The stack that's equipped.
	 * @param actor The actor equipping this stack.
	 * @return The dynamic modifiers to add on top of other modifiers on this stack. If none, return {@link #EMPTY}.
	 */
	default Multimap<String, EntityAttributeModifier> getDynamicModifiers(EquipmentSlot slot, ItemStack stack, ToolActor<?> actor) {
		return EMPTY;
	}
}
