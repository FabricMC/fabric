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

package net.fabricmc.fabric.test.enchanting;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.enchanting.v1.EnchantingPowerProvider;
import net.fabricmc.fabric.api.enchanting.v1.FabricEnchantment;

public class EnchantingTests implements ModInitializer {
	@Override
	public void onInitialize() {
		Registry.register(Registry.ENCHANTMENT, new Identifier("fabric", "test"), new TestEnchantment(Enchantment.Weight.COMMON, EquipmentSlot.values()));
		Registry.register(Registry.BLOCK, new Identifier("fabric", "enchanting_power_block"), new TestBlock(Block.Settings.copy(Blocks.STONE)));
	}

	static class TestEnchantment extends Enchantment implements FabricEnchantment {
		protected TestEnchantment(Weight weight, EquipmentSlot[] equipmentSlots) {
			super(weight, EnchantmentTarget.ALL, equipmentSlots);
		}

		@Override
		public boolean canPlayerEnchant(ItemStack stack) {
			return isAcceptableItem(stack);
		}

		@Override
		public boolean isAcceptableItem(ItemStack stack) {
			return stack.getItem() == Items.GOLDEN_HOE;
		}
	}

	static class TestBlock extends Block implements EnchantingPowerProvider {
		TestBlock(Settings settings) {
			super(settings);
		}

		@Override
		public int getEnchantingPower(BlockState blockState, World world, BlockPos blockPos) {
			return 3;
		}
	}
}
