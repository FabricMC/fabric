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

import org.jetbrains.annotations.Nullable;

import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;

public final class FoodGameInitializer implements ModInitializer {
	public static final Item DAMAGE = Registry.register(Registries.ITEM, new Identifier("fabric-item-api-v1-testmod", "damage_food"), new DamageFood(new FabricItemSettings().maxDamage(20)));
	public static final Item NAME = Registry.register(Registries.ITEM, new Identifier("fabric-item-api-v1-testmod", "name_food"), new NameFood(new FabricItemSettings()));

	@Override
	public void onInitialize() {
	}

	public static class DamageFood extends Item {
		public DamageFood(Settings settings) {
			super(settings);
		}

		@Override
		public @Nullable FoodComponent getFoodComponent(ItemStack stack) {
			return new FoodComponent.Builder()
					.hunger(20 - 20 * stack.getDamage() / stack.getMaxDamage())
					.saturationModifier(0.5f)
					.build();
		}
	}

	public static class NameFood extends Item {
		public NameFood(Settings settings) {
			super(settings);
		}

		@Override
		public @Nullable FoodComponent getFoodComponent(ItemStack stack) {
			return Registries.ITEM.get(new Identifier(stack.getName().getString())).getFoodComponent(stack);
		}
	}
}
