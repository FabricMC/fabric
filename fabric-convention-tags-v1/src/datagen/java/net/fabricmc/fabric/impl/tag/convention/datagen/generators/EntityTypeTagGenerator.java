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

import net.minecraft.entity.EntityType;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalEntityTypeTags;

public class EntityTypeTagGenerator extends FabricTagProvider.EntityTypeTagProvider {
	public EntityTypeTagGenerator(FabricDataGenerator dataGenerator) {
		super(dataGenerator);
	}

	@Override
	protected void generateTags() {
		getOrCreateTagBuilder(ConventionalEntityTypeTags.BOSSES)
				.add(EntityType.ENDER_DRAGON)
				.add(EntityType.WITHER);
		getOrCreateTagBuilder(ConventionalEntityTypeTags.MINECARTS)
				.add(EntityType.MINECART)
				.add(EntityType.TNT_MINECART)
				.add(EntityType.CHEST_MINECART)
				.add(EntityType.FURNACE_MINECART)
				.add(EntityType.COMMAND_BLOCK_MINECART)
				.add(EntityType.HOPPER_MINECART)
				.add(EntityType.SPAWNER_MINECART);
		getOrCreateTagBuilder(ConventionalEntityTypeTags.BOATS)
				.add(EntityType.BOAT);
	}
}
