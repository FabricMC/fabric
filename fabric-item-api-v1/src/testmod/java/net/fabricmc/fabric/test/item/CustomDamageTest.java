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

package net.fabricmc.fabric.test.item;

import net.minecraft.component.DataComponentType;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.CustomDamageHandler;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistry;

public class CustomDamageTest implements ModInitializer {
	public static final DataComponentType<Integer> WEIRD = Registry.register(Registries.DATA_COMPONENT_TYPE, new Identifier("fabric-item-api-v1-testmod", "weird"),
																DataComponentType.<Integer>builder().codec(Codecs.NONNEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT).build());
	public static final CustomDamageHandler WEIRD_DAMAGE_HANDLER = (stack, amount, entity, slot, breakCallback) -> {
		// If sneaking, apply all damage to vanilla. Otherwise, increment a tag on the stack by one and don't apply any damage
		if (entity.isSneaking()) {
			return amount;
		} else {
			stack.set(WEIRD, stack.getOrDefault(WEIRD, 0) + 1);
			return 0;
		}
	};
	// Do this static init *after* the damage handler otherwise it's still null while inside the constructor
	public static final Item WEIRD_PICK = new WeirdPick();

	@Override
	public void onInitialize() {
		Registry.register(Registries.ITEM, new Identifier("fabric-item-api-v1-testmod", "weird_pickaxe"), WEIRD_PICK);
		FuelRegistry.INSTANCE.add(WEIRD_PICK, 200);
		FabricBrewingRecipeRegistry.registerPotionRecipe(Potions.WATER, Ingredient.ofItems(WEIRD_PICK), Potions.AWKWARD);
		EnchantmentEvents.ALLOW_ENCHANTING.register(((enchantment, target, enchantingContext) -> {
			if (target.isOf(WEIRD_PICK)) {
				if (enchantment == Enchantments.SHARPNESS) {
					return ActionResult.SUCCESS;
				} else if (enchantment == Enchantments.FORTUNE) {
					return ActionResult.FAIL;
				}
			}

			return ActionResult.PASS;
		}));
	}

	public static final ItemEnchantmentsComponent INTRINSIC_ENCHANTMENTS = Util.make(
			new ItemEnchantmentsComponent.Builder(ItemEnchantmentsComponent.DEFAULT),
			builder -> {
				builder.set(Enchantments.SILK_TOUCH, 1);
				builder.set(Enchantments.FIRE_ASPECT, 1);
			}
	).build();

	public static class WeirdPick extends PickaxeItem {
		protected WeirdPick() {
			super(ToolMaterials.GOLD, new Item.Settings().customDamage(WEIRD_DAMAGE_HANDLER));
		}

		@Override
		public Text getName(ItemStack stack) {
			int v = stack.getOrDefault(WEIRD, 0);
			return super.getName(stack).copy().append(" (Weird Value: " + v + ")");
		}

		@Override
		public ItemStack getRecipeRemainder(ItemStack stack) {
			if (stack.getDamage() < stack.getMaxDamage() - 1) {
				ItemStack moreDamaged = stack.copy();
				moreDamaged.setCount(1);
				moreDamaged.setDamage(stack.getDamage() + 1);
				return moreDamaged;
			}

			return ItemStack.EMPTY;
		}

		@Override
		public ItemEnchantmentsComponent getIntrinsicEnchantments(ItemStack stack) {
			if (stack.isDamaged()) {
				System.out.println("Damaged, adding silk touch");
				return INTRINSIC_ENCHANTMENTS;
			} else {
				return super.getIntrinsicEnchantments(stack);
			}
		}
	}
}
