package net.fabricmc.fabric.impl.v1.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.impl.v1.datagen.generators.BiomeTagGenerator;
import net.fabricmc.fabric.impl.v1.datagen.generators.BlockTagGenerator;
import net.fabricmc.fabric.impl.v1.datagen.generators.EnchantmentTagGenerator;
import net.fabricmc.fabric.impl.v1.datagen.generators.EntityTypeTagGenerator;
import net.fabricmc.fabric.impl.v1.datagen.generators.FluidTagGenerator;
import net.fabricmc.fabric.impl.v1.datagen.generators.ItemTagGenerator;

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
