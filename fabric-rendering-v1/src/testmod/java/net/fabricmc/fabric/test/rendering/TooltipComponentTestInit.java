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

package net.fabricmc.fabric.test.rendering;

import java.util.Optional;

import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;

public class TooltipComponentTestInit implements ModInitializer {
	public static Item CUSTOM_TOOLTIP_ITEM = new CustomTooltipItem();
	public static Item CUSTOM_ARMOR_ITEM = new ArmorItem(TestArmorMaterial.INSTANCE, ArmorItem.Type.CHESTPLATE, new Item.Settings());

	@Override
	public void onInitialize() {
		Registry.register(Registries.ITEM, new Identifier("fabric-rendering-v1-testmod", "custom_tooltip"), CUSTOM_TOOLTIP_ITEM);
		Registry.register(Registries.ITEM, new Identifier("fabric-rendering-v1-testmod", "test_chest"), CUSTOM_ARMOR_ITEM);
	}

	private static class CustomTooltipItem extends Item {
		CustomTooltipItem() {
			super(new Settings());
		}

		@Override
		public Optional<TooltipData> getTooltipData(ItemStack stack) {
			return Optional.of(new Data(stack.getTranslationKey()));
		}
	}

	public record Data(String string) implements TooltipData {
	}

	public static final class TestArmorMaterial implements ArmorMaterial {
		public static final TestArmorMaterial INSTANCE = new TestArmorMaterial();

		private TestArmorMaterial() {
		}

		@Override
		public int getDurability(ArmorItem.Type type) {
			return 0;
		}

		@Override
		public int getProtection(ArmorItem.Type type) {
			return 0;
		}

		@Override
		public int getEnchantability() {
			return 0;
		}

		@Override
		public SoundEvent getEquipSound() {
			return SoundEvents.ITEM_ARMOR_EQUIP_LEATHER;
		}

		@Override
		public Ingredient getRepairIngredient() {
			return Ingredient.ofItems(Items.LEATHER);
		}

		@Override
		public String getName() {
			return "fabric-rendering-v1-testmod:test";
		}

		@Override
		public float getToughness() {
			return 0;
		}

		@Override
		public float getKnockbackResistance() {
			return 0;
		}
	}
}
