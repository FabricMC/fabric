package net.fabricmc.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.enchantment.FabricEnchantment;
import net.fabricmc.fabric.api.enchantment.FabricEnchantmentTarget;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;

public class EnchantmentMod implements ModInitializer {
	@Override
	public void onInitialize() {
		Registry.register(Registry.ENCHANTMENT, new Identifier("fabric", "test"), new TestEnchantment(Enchantment.Weight.COMMON, EquipmentSlot.values()));
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
}
