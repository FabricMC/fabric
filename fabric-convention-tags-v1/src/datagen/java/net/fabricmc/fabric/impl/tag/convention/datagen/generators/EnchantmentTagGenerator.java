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

package net.fabricmc.fabric.impl.tag.convention.datagen.generators;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalEnchantmentTags;

public class EnchantmentTagGenerator extends FabricTagProvider<Enchantment> {
	public EnchantmentTagGenerator(FabricDataGenerator dataGenerator) {
		super(dataGenerator, Registry.ENCHANTMENT, "Enchantment Tags");
	}

	@Override
	protected void generateTags() {
		getOrCreateTagBuilder(ConventionalEnchantmentTags.INCREASES_BLOCK_DROPS)
				.add(Enchantments.FORTUNE);
		getOrCreateTagBuilder(ConventionalEnchantmentTags.INCREASES_ENTITY_DROPS)
				.add(Enchantments.LOOTING);
		getOrCreateTagBuilder(ConventionalEnchantmentTags.WEAPON_DAMAGE_ENHANCEMENT)
				.add(Enchantments.BANE_OF_ARTHROPODS)
				.add(Enchantments.IMPALING)
				.add(Enchantments.SMITE)
				.add(Enchantments.POWER)
				.add(Enchantments.SHARPNESS);
		getOrCreateTagBuilder(ConventionalEnchantmentTags.ENTITY_MOVEMENT_ENHANCEMENT)
				.add(Enchantments.DEPTH_STRIDER)
				.add(Enchantments.SOUL_SPEED);
		getOrCreateTagBuilder(ConventionalEnchantmentTags.ENTITY_DEFENSE_ENHANCEMENT)
				.add(Enchantments.FEATHER_FALLING)
				.add(Enchantments.PROTECTION)
				.add(Enchantments.BLAST_PROTECTION)
				.add(Enchantments.PROJECTILE_PROTECTION)
				.add(Enchantments.FIRE_PROTECTION)
				.add(Enchantments.RESPIRATION);
	}
}
