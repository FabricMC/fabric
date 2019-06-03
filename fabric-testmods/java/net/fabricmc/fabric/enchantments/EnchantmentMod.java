package net.fabricmc.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.enchantment.EnchantingPowerProvider;
import net.fabricmc.fabric.api.enchantment.FabricEnchantment;
import net.fabricmc.fabric.api.enchantment.FabricEnchantmentTarget;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class EnchantmentMod implements ModInitializer {
	@Override
	public void onInitialize() {
		Registry.register(Registry.ENCHANTMENT, new Identifier("fabric", "test"), new TestEnchantment(Enchantment.Weight.COMMON, EquipmentSlot.values()));
		Registry.register(Registry.BLOCK, new Identifier("fabric", "enchanting_power_block"), new TestBlock(Block.Settings.copy(Blocks.STONE)));
	}

	static class TestEnchantment extends Enchantment implements FabricEnchantment {
		protected TestEnchantment(Weight enchantment$Weight_1, EquipmentSlot[] equipmentSlots_1) {
			super(enchantment$Weight_1, EnchantmentTarget.ALL, equipmentSlots_1);
		}

		@Override
		public FabricEnchantmentTarget getEnchantmentTarget() {
			return new TestTarget();
		}
	}

	static class TestTarget implements FabricEnchantmentTarget {
		@Override
		public boolean isAcceptableItem(Item item) {
			return item == Items.STONE_SWORD;
		}
	}

	static class TestBlock extends Block implements EnchantingPowerProvider {
		public TestBlock(Settings block$Settings_1) {
			super(block$Settings_1);
		}

		@Override
		public int getEnchantingPower(BlockState blockState, World world, BlockPos blockPos) {
			return 3;
		}
	}
}
