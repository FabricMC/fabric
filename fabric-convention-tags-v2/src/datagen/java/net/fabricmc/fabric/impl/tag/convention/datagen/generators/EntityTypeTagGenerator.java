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

import java.util.concurrent.CompletableFuture;

import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryWrapper;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalEntityTypeTags;

public final class EntityTypeTagGenerator extends FabricTagProvider.EntityTypeTagProvider {
	public EntityTypeTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
		super(output, completableFuture);
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup registries) {
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
				.add(EntityType.BOAT)
				.add(EntityType.CHEST_BOAT);
		getOrCreateTagBuilder(ConventionalEntityTypeTags.ANIMALS)
				.add(EntityType.ARMADILLO)
				.add(EntityType.AXOLOTL)
				.add(EntityType.BEE)
				.add(EntityType.CAMEL)
				.add(EntityType.CAT)
				.add(EntityType.CHICKEN)
				.add(EntityType.COD)
				.add(EntityType.COW)
				.add(EntityType.DOLPHIN)
				.add(EntityType.DONKEY)
				.add(EntityType.FOX)
				.add(EntityType.FROG)
				.add(EntityType.GLOW_SQUID)
				.add(EntityType.GOAT)
				.add(EntityType.HOGLIN)
				.add(EntityType.HORSE)
				.add(EntityType.LLAMA)
				.add(EntityType.MOOSHROOM)
				.add(EntityType.MULE)
				.add(EntityType.OCELOT)
				.add(EntityType.PANDA)
				.add(EntityType.PARROT)
				.add(EntityType.PIG)
				.add(EntityType.POLAR_BEAR)
				.add(EntityType.PUFFERFISH)
				.add(EntityType.RABBIT)
				.add(EntityType.SALMON)
				.add(EntityType.SHEEP)
				.add(EntityType.SKELETON_HORSE)
				.add(EntityType.SNIFFER)
				.add(EntityType.SQUID)
				.add(EntityType.STRIDER)
				.add(EntityType.TADPOLE)
				.add(EntityType.TRADER_LLAMA)
				.add(EntityType.TROPICAL_FISH)
				.add(EntityType.TURTLE)
				.add(EntityType.WOLF)
				.add(EntityType.ZOMBIE_HORSE);
		getOrCreateTagBuilder(ConventionalEntityTypeTags.CAPTURING_NOT_SUPPORTED);
		getOrCreateTagBuilder(ConventionalEntityTypeTags.TELEPORTING_NOT_SUPPORTED);
	}
}
