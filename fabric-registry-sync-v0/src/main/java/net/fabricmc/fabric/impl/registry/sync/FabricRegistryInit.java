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

package net.fabricmc.fabric.impl.registry.sync;

import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.event.registry.RegistryAttributeHolder;

public class FabricRegistryInit implements ModInitializer {
	@Override
	public void onInitialize() {
		// RegistryAttribute.SYNCED in PlaySoundS2CPacket.
		RegistryAttributeHolder.get(Registry.SOUND_EVENT)
				.addAttribute(RegistryAttribute.SYNCED);

		// RegistryAttribute.SYNCED with RegistryTagContainer from RegistryTagManager.
		RegistryAttributeHolder.get(Registry.FLUID)
				.addAttribute(RegistryAttribute.SYNCED);

		// StatusEffectInstance serialises with raw id.
		RegistryAttributeHolder.get(Registry.STATUS_EFFECT)
				.addAttribute(RegistryAttribute.SYNCED)
				.addAttribute(RegistryAttribute.PERSISTED);

		// RegistryAttribute.SYNCED in ChunkDeltaUpdateS2CPacket among other places, a pallet is used when saving.
		RegistryAttributeHolder.get(Registry.BLOCK)
				.addAttribute(RegistryAttribute.SYNCED);

		// Does not appear to be saved or RegistryAttribute.SYNCED, the string id is used.
		RegistryAttributeHolder.get(Registry.ENCHANTMENT);

		// RegistryAttribute.SYNCED in EntitySpawnS2CPacket
		RegistryAttributeHolder.get(Registry.ENTITY_TYPE)
				.addAttribute(RegistryAttribute.SYNCED);

		// Doesnt appear to need syncing or saving?
		RegistryAttributeHolder.get(Registry.ITEM)
				.addAttribute(RegistryAttribute.SYNCED);

		// Saved and RegistryAttribute.SYNCED using string ID.
		RegistryAttributeHolder.get(Registry.POTION);

		// Doesnt seem to be accessed apart from registering?
		RegistryAttributeHolder.get(Registry.CARVER);

		// Doesnt seem to be accessed apart from registering?
		RegistryAttributeHolder.get(Registry.SURFACE_BUILDER);

		// Serialised by string, doesnt seem to be RegistryAttribute.SYNCED
		RegistryAttributeHolder.get(Registry.FEATURE);

		// Serialised by string, doesnt seem to be RegistryAttribute.SYNCED
		RegistryAttributeHolder.get(Registry.DECORATOR);

		// Saved to level format
		RegistryAttributeHolder.get(Registry.BIOME)
				.addAttribute(RegistryAttribute.SYNCED)
				.addAttribute(RegistryAttribute.PERSISTED);

		// Serialised by string, doesnt seem to be RegistryAttribute.SYNCED
		RegistryAttributeHolder.get(Registry.BLOCK_STATE_PROVIDER_TYPE);

		// Serialised by string, doesnt seem to be RegistryAttribute.SYNCED
		RegistryAttributeHolder.get(Registry.BLOCK_PLACER_TYPE);

		// Serialised by string, doesnt seem to be RegistryAttribute.SYNCED
		RegistryAttributeHolder.get(Registry.FOLIAGE_PLACER_TYPE);

		// Serialised by string, doesnt seem to be RegistryAttribute.SYNCED
		RegistryAttributeHolder.get(Registry.TRUNK_PLACER_TYPE);

		// Serialised by string, doesnt seem to be RegistryAttribute.SYNCED
		RegistryAttributeHolder.get(Registry.TREE_DECORATOR_TYPE);

		// Serialised by string, doesnt seem to be RegistryAttribute.SYNCED
		RegistryAttributeHolder.get(Registry.FEATURE_SIZE_TYPE);

		// RegistryAttribute.SYNCED in ParticleS2CPacket
		RegistryAttributeHolder.get(Registry.PARTICLE_TYPE)
				.addAttribute(RegistryAttribute.SYNCED);

		// Serialised by string, doesnt seem to be RegistryAttribute.SYNCED
		RegistryAttributeHolder.get(Registry.BIOME_SOURCE_TYPE);

		// Serialised by string, doesnt seem to be RegistryAttribute.SYNCED
		RegistryAttributeHolder.get(Registry.CHUNK_GENERATOR_TYPE);

		// RegistryAttribute.SYNCED in GameJoinS2CPacket and PlayerRespawnS2CPacket. Entities and maps also use the id when saving.
		RegistryAttributeHolder.get(Registry.DIMENSION_TYPE)
				.addAttribute(RegistryAttribute.SYNCED)
				.addAttribute(RegistryAttribute.PERSISTED);

		// RegistryAttribute.SYNCED in PaintingSpawnS2CPacket
		RegistryAttributeHolder.get(Registry.PAINTING_MOTIVE)
				.addAttribute(RegistryAttribute.SYNCED);

		// Doesnt seem to be RegistryAttribute.SYNCED or saved, STAT_TYPE seems to handle the syncing.
		RegistryAttributeHolder.get(Registry.CUSTOM_STAT);

		RegistryAttributeHolder.get(Registry.CHUNK_STATUS);

		RegistryAttributeHolder.get(Registry.STRUCTURE_FEATURE);

		RegistryAttributeHolder.get(Registry.STRUCTURE_PIECE);

		RegistryAttributeHolder.get(Registry.RULE_TEST);

		RegistryAttributeHolder.get(Registry.POS_RULE_TEST);

		RegistryAttributeHolder.get(Registry.STRUCTURE_PROCESSOR);

		RegistryAttributeHolder.get(Registry.STRUCTURE_POOL_ELEMENT);

		// RegistryAttribute.SYNCED in OpenScreenS2CPacket
		RegistryAttributeHolder.get(Registry.SCREEN_HANDLER)
				.addAttribute(RegistryAttribute.SYNCED);

		RegistryAttributeHolder.get(Registry.RECIPE_TYPE);

		RegistryAttributeHolder.get(Registry.RECIPE_SERIALIZER);

		RegistryAttributeHolder.get(Registry.ATTRIBUTES);

		// RegistryAttribute.SYNCED in StatisticsS2CPacket
		RegistryAttributeHolder.get(Registry.STAT_TYPE)
				.addAttribute(RegistryAttribute.SYNCED);

		RegistryAttributeHolder.get(Registry.VILLAGER_TYPE);

		RegistryAttributeHolder.get(Registry.VILLAGER_PROFESSION);

		RegistryAttributeHolder.get(Registry.POINT_OF_INTEREST_TYPE);

		RegistryAttributeHolder.get(Registry.MEMORY_MODULE_TYPE);

		RegistryAttributeHolder.get(Registry.SENSOR_TYPE);

		RegistryAttributeHolder.get(Registry.SCHEDULE);

		RegistryAttributeHolder.get(Registry.ACTIVITY);
	}
}
