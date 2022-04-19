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

package net.fabricmc.fabric.impl.tag.convention.datagen;

import net.fabricmc.fabric.impl.tag.convention.datagen.generators.BiomeTagGenerator;
import net.fabricmc.fabric.impl.tag.convention.datagen.generators.BlockTagGenerator;
import net.fabricmc.fabric.impl.tag.convention.datagen.generators.EnchantmentTagGenerator;
import net.fabricmc.fabric.impl.tag.convention.datagen.generators.EntityTypeTagGenerator;
import net.fabricmc.fabric.impl.tag.convention.datagen.generators.FluidTagGenerator;
import net.fabricmc.fabric.impl.tag.convention.datagen.generators.ItemTagGenerator;
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
