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

package net.fabricmc.fabric.api.item.v1;

import net.fabricmc.fabric.mixin.item.ItemEntityAccessor;

import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import net.fabricmc.fabric.impl.item.FabricItemInternals;

/**
 * General-purpose Fabric-provided extensions for {@link Item} subclasses.
 *
 * <p>Note: This interface is automatically implemented on all items via Mixin and interface injection.
 *
 * <p>Note to maintainers: Functions should only be added to this interface if they are general-purpose enough,
 * to be evaluated on a case-by-case basis. Otherwise, they are better suited for more specialized APIs.
 */
public interface FabricItem {
	/**
	 * When the components of an item stack in the main hand or off hand changes, vanilla runs an "update animation".
	 * This function is called on the client side when the components or count of the stack has changed, but not the item,
	 * and returning false cancels this animation.
	 *
	 * @param player   the current player; this may be safely cast to {@link net.minecraft.client.network.ClientPlayerEntity} in client-only code
	 * @param hand     the hand; this function applies both to the main hand and the off hand
	 * @param oldStack the previous stack, of this item
	 * @param newStack the new stack, also of this item
	 * @return true to run the vanilla animation, false to cancel it.
	 */
	default boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
		return true;
	}

	/**
	 * When the components of the selected stack changes, block breaking progress is reset.
	 * This function is called when the components of the selected stack has changed,
	 * and returning true allows the block breaking progress to continue.
	 *
	 * @param player   the player breaking the block
	 * @param oldStack the previous stack, of this item
	 * @param newStack the new stack, also of this item
	 * @return true to allow continuing block breaking, false to reset the progress.
	 */
	default boolean allowContinuingBlockBreaking(PlayerEntity player, ItemStack oldStack, ItemStack newStack) {
		return false;
	}

	/**
	 * Return the attribute modifiers to apply when this stack is worn in a living entity equipment slot.
	 * Stack-aware version of {@link Item#getAttributeModifiers()}.
	 *
	 * <p>Note that attribute modifiers are only updated when the stack changes, i.e. when {@code ItemStack.areEqual(old, new)} is false.
	 *
	 * @param stack the current stack
	 * @return the attribute modifiers
	 */
	default AttributeModifiersComponent getAttributeModifiers(ItemStack stack) {
		return ((Item) this).getAttributeModifiers();
	}

	/**
	 * Returns a leftover item stack after {@code stack} is consumed in a recipe.
	 * (This is also known as "recipe remainder".)
	 * For example, using a lava bucket in a furnace as fuel will leave an empty bucket.
	 *
	 * <p>Here is an example for a recipe remainder that increments the item's damage.
	 *
	 * <pre>{@code
	 *  if (stack.getDamage() < stack.getMaxDamage() - 1) {
	 *  	ItemStack moreDamaged = stack.copy();
	 *  	moreDamaged.setDamage(stack.getDamage() + 1);
	 *  	return moreDamaged;
	 *  }
	 *
	 *  return ItemStack.EMPTY;
	 * }</pre>
	 *
	 *
	 * <p>This is a stack-aware version of {@link Item#getRecipeRemainder()}.
	 *
	 * <p>Note that simple item remainders can also be set via {@link Item.Settings#recipeRemainder(Item)}.
	 *
	 * <p>If you want to get a remainder for a stack,
	 * is recommended to use the stack version of this method: {@link FabricItemStack#getRecipeRemainder()}.
	 *
	 * @param stack the consumed {@link ItemStack}
	 * @return the leftover item stack
	 */
	default ItemStack getRecipeRemainder(ItemStack stack) {
		return ((Item) this).hasRecipeRemainder() ? ((Item) this).getRecipeRemainder().getDefaultStack() : ItemStack.EMPTY;
	}

	/**
	 * Determines if the item is allowed to receive an {@link Enchantment}. This can be used to manually override what
	 * enchantments a modded item is able to receive.
	 *
	 * <p>For example, one might want a modded item to be able to receive Unbreaking, but not Mending, which cannot be
	 * achieved with the vanilla tag system alone. Alternatively, one might want to do the same thing with enchantments
	 * from other mods, which don't have a similar tag system in general.</p>
	 *
	 * <p>Note that this method is only called <em>after</em> the {@link EnchantmentEvents#ALLOW_ENCHANTING} event, and
	 * only if none of the listeners to that event override the result.</p>
	 *
	 * @param stack       the current stack
	 * @param enchantment the enchantment to check
	 * @param context     the context in which the enchantment is being checked
	 * @return whether the enchantment is allowed to apply to the stack
	 */
	default boolean canBeEnchantedWith(ItemStack stack, Enchantment enchantment, EnchantingContext context) {
		return enchantment.isAcceptableItem(stack);
	}

	/**
	 * Retrieves the normal 'lifespan' of this item when it is dropped on the ground
	 * as an ItemEntity. This is in ticks, standard result is 6000, or 5 mins.
	 *
	 * @param stack The current ItemStack
	 * @param world The level the entity is in
	 * @return The normal lifespan in ticks.
	 */
	default int getEntityLifespan(ItemStack stack, World world) {
		return ItemEntityAccessor.getDespawnAge();
	}

	/**
	 * Fabric-provided extensions for {@link Item.Settings}.
	 * This interface is automatically implemented on all item settings via Mixin and interface injection.
	 */
	interface Settings {
		/**
		 * Sets the equipment slot provider of the item.
		 *
		 * @param equipmentSlotProvider the equipment slot provider
		 * @return this builder
		 */
		default Item.Settings equipmentSlot(EquipmentSlotProvider equipmentSlotProvider) {
			FabricItemInternals.computeExtraData((Item.Settings) this).equipmentSlot(equipmentSlotProvider);
			return (Item.Settings) this;
		}

		/**
		 * Sets the custom damage handler of the item.
		 * Note that this is only called on an ItemStack if {@link ItemStack#isDamageable()} returns true.
		 *
		 * @see CustomDamageHandler
		 */
		default Item.Settings customDamage(CustomDamageHandler handler) {
			FabricItemInternals.computeExtraData((Item.Settings) this).customDamage(handler);
			return (Item.Settings) this;
		}
	}
}
