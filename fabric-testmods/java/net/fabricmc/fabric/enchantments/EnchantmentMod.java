package net.fabricmc.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.enchantment.enchantment.EnchantingPowerProvider;
import net.fabricmc.fabric.api.enchantment.enchantment.FabricEnchantment;
import net.fabricmc.fabric.api.enchantment.enchantment.FabricEnchantmentTarget;
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
	public static final TestTarget TEST_ENCHANTMENT_TARGET = new TestTarget();

	@Override
	public void onInitialize() {
		Registry.register(Registry.ENCHANTMENT, new Identifier("fabric", "test"), new TestEnchantment(Enchantment.Weight.COMMON, EquipmentSlot.values()));
		EnchantmentTargetRegistry.INSTANCE.register(TEST_ENCHANTMENT_TARGET);
		Registry.register(Registry.BLOCK, new Identifier("fabric", "enchanting_power_block"), new TestBlock(Block.Settings.copy(Blocks.STONE)));
	}

	static class TestEnchantment extends Enchantment implements FabricEnchantment {
		protected TestEnchantment(Weight weight, EquipmentSlot[] equipmentSlots) {
			super(weight, EnchantmentTarget.ALL, equipmentSlots);
		}

		@Override
		public FabricEnchantmentTarget getEnchantmentTarget() {
			return TEST_ENCHANTMENT_TARGET;
		}
	}

	static class TestTarget implements FabricEnchantmentTarget {
		@Override
		public boolean isAcceptableItem(Item item) {
			return item == Items.STONE_SWORD;
		}
	}

	static class TestBlock extends Block implements EnchantingPowerProvider {
		public TestBlock(Settings settings) {
			super(settings);
		}

		@Override
		public int getEnchantingPower(BlockState blockState, World world, BlockPos blockPos) {
			return 3;
		}
	}
}
