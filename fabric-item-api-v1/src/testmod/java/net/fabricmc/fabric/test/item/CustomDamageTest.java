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

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.Potions;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.CustomDamageHandler;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.test.item.mixin.BrewingRecipeRegistryAccessor;

public class CustomDamageTest implements ModInitializer {
	public static final Item WEIRD_PICK = new WeirdPick();

	@Override
	public void onInitialize() {
		Registry.register(Registries.ITEM, new Identifier("fabric-item-api-v1-testmod", "weird_pickaxe"), WEIRD_PICK);
		FuelRegistry.INSTANCE.add(WEIRD_PICK, 200);
		BrewingRecipeRegistryAccessor.callRegisterPotionRecipe(Potions.WATER, WEIRD_PICK, Potions.AWKWARD);
	}

	public static final CustomDamageHandler WEIRD_DAMAGE_HANDLER = (stack, amount, entity, breakCallback) -> {
		// If sneaking, apply all damage to vanilla. Otherwise, increment a tag on the stack by one and don't apply any damage
		if (entity.isSneaking()) {
			return amount;
		} else {
			NbtCompound tag = stack.getOrCreateNbt();
			tag.putInt("weird", tag.getInt("weird") + 1);
			return 0;
		}
	};

	public static class WeirdPick extends PickaxeItem {
		protected WeirdPick() {
			super(ToolMaterials.GOLD, 1, -2.8F, new FabricItemSettings().customDamage(WEIRD_DAMAGE_HANDLER));
		}

		@Override
		public Text getName(ItemStack stack) {
			int v = stack.getOrCreateNbt().getInt("weird");
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
	}
}
