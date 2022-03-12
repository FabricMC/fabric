package net.fabricmc.fabric.impl.tag.common.datagen;

import net.fabricmc.fabric.impl.tag.common.datagen.generators.BiomeTagGenerator;
import net.fabricmc.fabric.impl.tag.common.datagen.generators.BlockTagGenerator;
import net.fabricmc.fabric.impl.tag.common.datagen.generators.EnchantmentTagGenerator;
import net.fabricmc.fabric.impl.tag.common.datagen.generators.EntityTypeTagGenerator;
import net.fabricmc.fabric.impl.tag.common.datagen.generators.FluidTagGenerator;
import net.fabricmc.fabric.impl.tag.common.datagen.generators.ItemTagGenerator;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class DatagenEntrypoint implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		fabricDataGenerator.addProvider(ItemTagGenerator::new);
		fabricDataGenerator.addProvider(FluidTagGenerator::new);
		fabricDataGenerator.addProvider(EnchantmentTagGenerator::new);
		fabricDataGenerator.addProvider(BlockTagGenerator::new);
		fabricDataGenerator.addProvider(BiomeTagGenerator::new);
		fabricDataGenerator.addProvider(EntityTypeTagGenerator::new);
	}
}
