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
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

/**
 * An abstract class for enchantments looking to take advantage of custom enchantment targets.
 *
 * <p>For an item to be enchanted in the enchantment table both
 * isAcceptableItem and isEnchantableItem must be true for the item.</p>
 *
 * <p>For an item to be enchanted in the anvil only isAcceptableItem
 * must be true for the item.</p>
 *
 * <p>Consequently, all enchantments an item can accept from the enchantment
 * table can also be applied through the anvil just like vanilla.</p>
 *
 * @author Vaerian (vaeriann@gmail.com or @Vaerian on GitHub).
 *
 * <p>Please contact the author, Vaerian, at the email or GitHub profile listed above
 * with any questions surrounding implementation choices, functionality, or updating
 * to newer versions of the game.</p>
 */
public abstract class FabricEnchantment extends Enchantment {
	protected FabricEnchantment(Rarity weight, EquipmentSlot...slotTypes) {
		super(weight, EnchantmentTarget.ARMOR /*This can be anything, it just can't be null otherwise it'll cause a null pointer exception. At this point it has no bearing on functionality*/, slotTypes);
	}

	// Making this method override and abstract means it has to be overwritten by any children.
	// By default vanilla defines this as simply delegating to the target, but obviously, because
	// there is no target anymore it must be overridden in order to prevent a null pointer exception.
	/**
	 * Overrides the vanilla enchantment target and determines whether or not
	 * the given item stack can accept this enchantment by any means.
	 *
	 * <p>Only this method is called directly by the anvil to determine whether or not
	 * this enchantment can be applied to an item. Item stacks that do not indicate
	 * enchantability ({@link net.minecraft.item.Item#isEnchantable(ItemStack)} and
	 * {@link net.fabricmc.fabric.api.enchantment.v1.FabricEnchantment#isEnchantableItem(ItemStack)})
	 * can still accept this enchantment through the anvil, but they will not be able to be enchanted
	 * in the enchantment table itself</p>
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
	 * Overrides the vanilla judgement about whether or not the given item
	 * stack can accept this enchantment from an enchantment table.
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
	 * stack should be able to accept the enchantment from an enchantment
	 * table.
	 *
	 * @see net.minecraft.item.Item#isEnchantable(ItemStack)
	 * @see net.minecraft.screen.EnchantmentScreenHandler#onContentChanged(Inventory)
	 * @see net.fabricmc.fabric.mixin.enchantment.EnchantmentHelperMixin
	 */
	public boolean isEnchantableItem(ItemStack stack) {
		return this.isAcceptableItem(stack);
	}

	/**
	 * Determines whether or not this enchantment's book ought to be placed into
	 * the given item group.
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
