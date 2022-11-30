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

import net.minecraft.registry.Registries;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.event.registry.RegistryAttributeHolder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class FabricRegistryInit implements ModInitializer {
	@Override
	public void onInitialize() {
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
				RegistrySyncManager.sendPacket(server, handler.player));

		// Synced in PlaySoundS2CPacket.
		RegistryAttributeHolder.get(Registries.SOUND_EVENT)
				.addAttribute(RegistryAttribute.SYNCED);

		// Synced with RegistryTagContainer from RegistryTagManager.
		RegistryAttributeHolder.get(Registries.FLUID)
				.addAttribute(RegistryAttribute.SYNCED);

		// StatusEffectInstance serialises with raw id.
		RegistryAttributeHolder.get(Registries.STATUS_EFFECT)
				.addAttribute(RegistryAttribute.SYNCED)
				.addAttribute(RegistryAttribute.PERSISTED);

		// Synced in ChunkDeltaUpdateS2CPacket among other places, a pallet is used when saving.
		RegistryAttributeHolder.get(Registries.BLOCK)
				.addAttribute(RegistryAttribute.SYNCED);

		// Synced in EnchantmentScreenHandler
		RegistryAttributeHolder.get(Registries.ENCHANTMENT)
				.addAttribute(RegistryAttribute.SYNCED);

		// Synced in EntitySpawnS2CPacket and RegistryTagManager
		RegistryAttributeHolder.get(Registries.ENTITY_TYPE)
				.addAttribute(RegistryAttribute.SYNCED);

		// Synced in RegistryTagManager
		RegistryAttributeHolder.get(Registries.ITEM)
				.addAttribute(RegistryAttribute.SYNCED);

		// Saved and synced using string ID.
		RegistryAttributeHolder.get(Registries.POTION);

		// Doesnt seem to be accessed apart from registering?
		RegistryAttributeHolder.get(Registries.CARVER);

		// Serialised by string, doesnt seem to be synced
		RegistryAttributeHolder.get(Registries.FEATURE);

		// Serialised by string, doesnt seem to be synced
		RegistryAttributeHolder.get(Registries.BLOCK_STATE_PROVIDER_TYPE);

		// Serialised by string, doesnt seem to be synced
		RegistryAttributeHolder.get(Registries.FOLIAGE_PLACER_TYPE);

		// Serialised by string, doesnt seem to be synced
		RegistryAttributeHolder.get(Registries.TRUNK_PLACER_TYPE);

		// Serialised by string, doesnt seem to be synced
		RegistryAttributeHolder.get(Registries.TREE_DECORATOR_TYPE);

		// Serialised by string, doesnt seem to be synced
		RegistryAttributeHolder.get(Registries.FEATURE_SIZE_TYPE);

		// Synced in ParticleS2CPacket
		RegistryAttributeHolder.get(Registries.PARTICLE_TYPE)
				.addAttribute(RegistryAttribute.SYNCED);

		// Serialised by string, doesnt seem to be synced
		RegistryAttributeHolder.get(Registries.BIOME_SOURCE);

		// Synced. Vanilla uses raw ids in BlockEntityUpdateS2CPacket, and mods use the Vanilla syncing since 1.18
		RegistryAttributeHolder.get(Registries.BLOCK_ENTITY_TYPE)
				.addAttribute(RegistryAttribute.SYNCED);

		// Synced in PaintingSpawnS2CPacket
		RegistryAttributeHolder.get(Registries.PAINTING_VARIANT)
				.addAttribute(RegistryAttribute.SYNCED);

		// Doesnt seem to be synced or saved, STAT_TYPE seems to handle the syncing.
		RegistryAttributeHolder.get(Registries.CUSTOM_STAT);

		// Serialised by string, doesnt seem to be synced
		RegistryAttributeHolder.get(Registries.CHUNK_STATUS);

		// Serialised by string, doesnt seem to be synced
		RegistryAttributeHolder.get(Registries.STRUCTURE_TYPE);

		// Serialised by string, doesnt seem to be synced
		RegistryAttributeHolder.get(Registries.STRUCTURE_PIECE);

		// Serialised by string, doesnt seem to be synced
		RegistryAttributeHolder.get(Registries.RULE_TEST);

		// Serialised by string, doesnt seem to be synced
		RegistryAttributeHolder.get(Registries.POS_RULE_TEST);

		RegistryAttributeHolder.get(Registries.STRUCTURE_PROCESSOR);

		// Serialised by string, doesnt seem to be synced
		RegistryAttributeHolder.get(Registries.STRUCTURE_POOL_ELEMENT);

		// Uses a data tracker (and thus, raw IDs) to sync cat entities to the client
		RegistryAttributeHolder.get(Registries.CAT_VARIANT)
				.addAttribute(RegistryAttribute.SYNCED);

		// Uses a data tracker (and thus, raw IDs) to sync frog entities to the client
		RegistryAttributeHolder.get(Registries.FROG_VARIANT)
				.addAttribute(RegistryAttribute.SYNCED);

		// Uses a data tracker (and thus, raw IDs) to sync painting entities to the client
		RegistryAttributeHolder.get(Registries.PAINTING_VARIANT)
				.addAttribute(RegistryAttribute.SYNCED);

		//  Uses the raw ID when syncing the command tree to the client
		RegistryAttributeHolder.get(Registries.COMMAND_ARGUMENT_TYPE)
				.addAttribute(RegistryAttribute.SYNCED);

		// Synced in OpenScreenS2CPacket
		RegistryAttributeHolder.get(Registries.SCREEN_HANDLER)
				.addAttribute(RegistryAttribute.SYNCED);

		// Does not seem to be serialised, only queried by id. Not synced
		RegistryAttributeHolder.get(Registries.RECIPE_TYPE);

		// Synced by id
		RegistryAttributeHolder.get(Registries.RECIPE_SERIALIZER);

		// Synced and saved by id
		RegistryAttributeHolder.get(Registries.ATTRIBUTE);

		// Synced in StatisticsS2CPacket
		RegistryAttributeHolder.get(Registries.STAT_TYPE)
				.addAttribute(RegistryAttribute.SYNCED);

		// Synced by rawID in TrackedDataHandlerRegistry.VILLAGER_DATA
		RegistryAttributeHolder.get(Registries.VILLAGER_TYPE)
				.addAttribute(RegistryAttribute.SYNCED);

		// Synced by rawID in TrackedDataHandlerRegistry.VILLAGER_DATA
		RegistryAttributeHolder.get(Registries.VILLAGER_PROFESSION)
				.addAttribute(RegistryAttribute.SYNCED);

		// Serialised by string, doesnt seem to be synced
		RegistryAttributeHolder.get(Registries.POINT_OF_INTEREST_TYPE);

		// Serialised by string, doesnt seem to be synced
		RegistryAttributeHolder.get(Registries.MEMORY_MODULE_TYPE);

		// Doesnt seem to be serialised or synced.
		RegistryAttributeHolder.get(Registries.SENSOR_TYPE);

		// Doesnt seem to be serialised or synced.
		RegistryAttributeHolder.get(Registries.SCHEDULE);

		// Doesnt seem to be serialised or synced.
		RegistryAttributeHolder.get(Registries.ACTIVITY);

		// Doesnt seem to be serialised or synced.
		RegistryAttributeHolder.get(Registries.LOOT_POOL_ENTRY_TYPE);

		// Doesnt seem to be serialised or synced.
		RegistryAttributeHolder.get(Registries.LOOT_FUNCTION_TYPE);

		// Doesnt seem to be serialised or synced.
		RegistryAttributeHolder.get(Registries.LOOT_CONDITION_TYPE);

		// Synced in TagManager::toPacket/fromPacket -> TagGroup::serialize/deserialize
		RegistryAttributeHolder.get(Registries.GAME_EVENT)
				.addAttribute(RegistryAttribute.SYNCED);
	}
}
