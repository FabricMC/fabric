package net.fabricmc.fabric.impl.common.tag.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.impl.common.tag.datagen.generators.BiomeTagGenerator;
import net.fabricmc.fabric.impl.common.tag.datagen.generators.BlockTagGenerator;
import net.fabricmc.fabric.impl.common.tag.datagen.generators.EnchantmentTagGenerator;
import net.fabricmc.fabric.impl.common.tag.datagen.generators.EntityTypeTagGenerator;
import net.fabricmc.fabric.impl.common.tag.datagen.generators.FluidTagGenerator;
import net.fabricmc.fabric.impl.common.tag.datagen.generators.ItemTagGenerator;

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
