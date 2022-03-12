package net.fabricmc.fabric.impl.tag.common.datagen.generators;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.v1.CommonEnchantmentTags;

public class EnchantmentTagGenerator extends FabricTagProvider<Enchantment> {
	public EnchantmentTagGenerator(FabricDataGenerator dataGenerator) {
		super(dataGenerator, Registry.ENCHANTMENT, "Enchantment Tags");
	}

	@Override
	protected void generateTags() {
		getOrCreateTagBuilder(CommonEnchantmentTags.INCREASES_BLOCK_DROPS)
				.add(Enchantments.FORTUNE);
		getOrCreateTagBuilder(CommonEnchantmentTags.INCREASES_ENTITY_DROPS)
				.add(Enchantments.LOOTING);
		getOrCreateTagBuilder(CommonEnchantmentTags.IDENTICAL_DROPS)
				.add(Enchantments.SILK_TOUCH);
		getOrCreateTagBuilder(CommonEnchantmentTags.WEAPON_DAMAGE_ENHANCEMENT)
				.add(Enchantments.BANE_OF_ARTHROPODS)
				.add(Enchantments.IMPALING)
				.add(Enchantments.SMITE)
				.add(Enchantments.POWER)
				.add(Enchantments.SHARPNESS);
	}
}
