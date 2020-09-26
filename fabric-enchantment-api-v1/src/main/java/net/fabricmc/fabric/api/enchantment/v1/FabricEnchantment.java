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

package net.fabricmc.fabric.api.enchantment.v1;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

/**
 * An abstract class for enchantments looking to take advantage of custom enchantment targets.
 */
public abstract class FabricEnchantment extends Enchantment {
	protected FabricEnchantment(Rarity weight, EquipmentSlot...slotTypes) {
		super(weight, null, slotTypes);
	}

	// Making this method override and abstract means it has to be overwritten by any children.
	// By default vanilla defines this as simply delegating to the target, but obviously, because
	// there is no target anymore it must be overridden in order to prevent a null pointer exception.
	/**
	 * Overrides the vanilla enchantment target and determines whether or not
	 * the given item stack can accept this enchantment.
	 *
	 * <p>It is important to note that item stacks of items who do not say that
	 * they are enchantable will not be able to be enchanted, despite this
	 * method indicating that they can accept this enchantment. To change this
	 * the {@link net.minecraft.item.Item#isEnchantable(ItemStack)} method
	 * must be overridden.</p>
	 *
	 * @param stack The item stack querying the ability to accept this
	 * enchantment.
	 * @return A boolean value representing whether or not the given item
	 * stack should be able to accept the enchantment.
	 *
	 * @see net.minecraft.item.Item#isEnchantable(ItemStack)
	 * @see net.minecraft.screen.EnchantmentScreenHandler#onContentChanged(Inventory)
	 * @see net.fabricmc.fabric.mixin.enchantment.EnchantmentHelperMixin
	 */
	@Override
	public abstract boolean isAcceptableItem(ItemStack stack);

	/**
	 * A custom method that determines whether or not this enchantment's books
	 * ought to be placed into the given item group.
	 *
	 * <p>It should be noted that all enchanted books, for all enchantments are
	 * automatically added to the search tab regardless of what this method
	 * says about the search tab.</p>
	 *
	 * @param group The item group querying whether or not to contain the
	 * enchanted books for this enchantment.
	 * @return A boolean value repressenting whether or not the given
	 * item group should contain the enchanted books for this enchantment.
	 *
	 * @see net.fabricmc.fabric.mixin.enchantment.EnchantedBookItemMixin
	 */
	public abstract boolean isAcceptableItemGroup(ItemGroup group);
}
