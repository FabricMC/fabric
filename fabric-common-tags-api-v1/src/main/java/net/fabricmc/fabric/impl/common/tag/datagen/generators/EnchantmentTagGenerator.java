package net.fabricmc.fabric.impl.common.tag.datagen.generators;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tags.v1.CommonEnchantmentTags;

public class EnchantmentTagGenerator extends FabricTagProvider<Enchantment> {
	public EnchantmentTagGenerator(FabricDataGenerator dataGenerator) {
		super(dataGenerator, Registry.ENCHANTMENT, "Enchantment Tags");
	}

	@Override
	protected void generateTags() {
		getOrCreateTagBuilder(CommonEnchantmentTags.FORTUNE)
				.add(Enchantments.FORTUNE);
		getOrCreateTagBuilder(CommonEnchantmentTags.LOOTING)
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
