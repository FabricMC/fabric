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
		// Synced in PlaySoundS2CPacket.
		RegistryAttributeHolder.get(Registry.SOUND_EVENT)
				.addAttribute(RegistryAttribute.SYNCED);

		// Synced with RegistryTagContainer from RegistryTagManager.
		RegistryAttributeHolder.get(Registry.FLUID)
				.addAttribute(RegistryAttribute.SYNCED);

		// StatusEffectInstance serialises with raw id.
		RegistryAttributeHolder.get(Registry.STATUS_EFFECT)
				.addAttribute(RegistryAttribute.SYNCED)
				.addAttribute(RegistryAttribute.PERSISTED);

		// Synced in ChunkDeltaUpdateS2CPacket among other places, a pallet is used when saving.
		RegistryAttributeHolder.get(Registry.BLOCK)
				.addAttribute(RegistryAttribute.SYNCED);

		// Synced in EnchantmentScreenHandler
		RegistryAttributeHolder.get(Registry.ENCHANTMENT)
				.addAttribute(RegistryAttribute.SYNCED);

		// Synced in EntitySpawnS2CPacket and RegistryTagManager
		RegistryAttributeHolder.get(Registry.ENTITY_TYPE)
				.addAttribute(RegistryAttribute.SYNCED);

		// Synced in RegistryTagManager
		RegistryAttributeHolder.get(Registry.ITEM)
				.addAttribute(RegistryAttribute.SYNCED);

		// Saved and synced using string ID.
		RegistryAttributeHolder.get(Registry.POTION);

		// Doesnt seem to be accessed apart from registering?
		RegistryAttributeHolder.get(Registry.CARVER);

		// Doesnt seem to be accessed apart from registering?
		RegistryAttributeHolder.get(Registry.SURFACE_BUILDER);

		// Serialised by string, doesnt seem to be synced
		RegistryAttributeHolder.get(Registry.FEATURE);

		// Serialised by string, doesnt seem to be synced
		RegistryAttributeHolder.get(Registry.DECORATOR);

		// Saved to level format
		RegistryAttributeHolder.get(Registry.BIOME)
				.addAttribute(RegistryAttribute.SYNCED)
				.addAttribute(RegistryAttribute.PERSISTED);

		// Serialised by string, doesnt seem to be synced
		RegistryAttributeHolder.get(Registry.BLOCK_STATE_PROVIDER_TYPE);

		// Serialised by string, doesnt seem to be synced
		RegistryAttributeHolder.get(Registry.BLOCK_PLACER_TYPE);

		// Serialised by string, doesnt seem to be synced
		RegistryAttributeHolder.get(Registry.FOLIAGE_PLACER_TYPE);

		// Serialised by string, doesnt seem to be synced
		RegistryAttributeHolder.get(Registry.TRUNK_PLACER_TYPE);

		// Serialised by string, doesnt seem to be synced
		RegistryAttributeHolder.get(Registry.TREE_DECORATOR_TYPE);

		// Serialised by string, doesnt seem to be synced
		RegistryAttributeHolder.get(Registry.FEATURE_SIZE_TYPE);

		// Synced in ParticleS2CPacket
		RegistryAttributeHolder.get(Registry.PARTICLE_TYPE)
				.addAttribute(RegistryAttribute.SYNCED);

		// Serialised by string, doesnt seem to be synced
		RegistryAttributeHolder.get(Registry.BIOME_SOURCE);

		// Not synced or saved
		RegistryAttributeHolder.get(Registry.BLOCK_ENTITY_TYPE);

		// Synced in PaintingSpawnS2CPacket
		RegistryAttributeHolder.get(Registry.PAINTING_MOTIVE)
				.addAttribute(RegistryAttribute.SYNCED);

		// Doesnt seem to be synced or saved, STAT_TYPE seems to handle the syncing.
		RegistryAttributeHolder.get(Registry.CUSTOM_STAT);

		// Serialised by string, doesnt seem to be synced
		RegistryAttributeHolder.get(Registry.CHUNK_STATUS);

		// Serialised by string, doesnt seem to be synced
		RegistryAttributeHolder.get(Registry.STRUCTURE_FEATURE);

		// Serialised by string, doesnt seem to be synced
		RegistryAttributeHolder.get(Registry.STRUCTURE_PIECE);

		// Serialised by string, doesnt seem to be synced
		RegistryAttributeHolder.get(Registry.RULE_TEST);

		// Serialised by string, doesnt seem to be synced
		RegistryAttributeHolder.get(Registry.POS_RULE_TEST);

		RegistryAttributeHolder.get(Registry.STRUCTURE_PROCESSOR);

		// Serialised by string, doesnt seem to be synced
		RegistryAttributeHolder.get(Registry.STRUCTURE_POOL_ELEMENT);

		// Synced in OpenScreenS2CPacket
		RegistryAttributeHolder.get(Registry.SCREEN_HANDLER)
				.addAttribute(RegistryAttribute.SYNCED);

		// Does not seem to be serialised, only queried by id. Not synced
		RegistryAttributeHolder.get(Registry.RECIPE_TYPE);

		// Synced by id
		RegistryAttributeHolder.get(Registry.RECIPE_SERIALIZER);

		// Synced and saved by id
		RegistryAttributeHolder.get(Registry.ATTRIBUTE);

		// Synced in StatisticsS2CPacket
		RegistryAttributeHolder.get(Registry.STAT_TYPE)
				.addAttribute(RegistryAttribute.SYNCED);

		// Synced by rawID in TrackedDataHandlerRegistry.VILLAGER_DATA
		RegistryAttributeHolder.get(Registry.VILLAGER_TYPE)
				.addAttribute(RegistryAttribute.SYNCED);

		// Synced by rawID in TrackedDataHandlerRegistry.VILLAGER_DATA
		RegistryAttributeHolder.get(Registry.VILLAGER_PROFESSION)
				.addAttribute(RegistryAttribute.SYNCED);

		// Serialised by string, doesnt seem to be synced
		RegistryAttributeHolder.get(Registry.POINT_OF_INTEREST_TYPE);

		// Serialised by string, doesnt seem to be synced
		RegistryAttributeHolder.get(Registry.MEMORY_MODULE_TYPE);

		// Doesnt seem to be serialised or synced.
		RegistryAttributeHolder.get(Registry.SENSOR_TYPE);

		// Doesnt seem to be serialised or synced.
		RegistryAttributeHolder.get(Registry.SCHEDULE);

		// Doesnt seem to be serialised or synced.
		RegistryAttributeHolder.get(Registry.ACTIVITY);

		// Doesnt seem to be serialised or synced.
		RegistryAttributeHolder.get(Registry.LOOT_POOL_ENTRY_TYPE);

		// Doesnt seem to be serialised or synced.
		RegistryAttributeHolder.get(Registry.LOOT_FUNCTION_TYPE);

		// Doesnt seem to be serialised or synced.
		RegistryAttributeHolder.get(Registry.LOOT_CONDITION_TYPE);
	}
}
