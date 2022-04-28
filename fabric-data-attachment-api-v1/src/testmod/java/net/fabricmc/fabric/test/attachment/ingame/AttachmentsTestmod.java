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

package net.fabricmc.fabric.test.attachment.ingame;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;

public class AttachmentsTestmod implements ModInitializer {
	public static Identifier id(String path) {
		return new Identifier("fabric-data-attachment-api-v1-testmod", path);
	}

	@Override
	public void onInitialize() {
		registerItem(new BlockEntityAdderItem(1, true), "client_block_entity_add");
		registerItem(new BlockEntityAdderItem(-1, true), "client_block_entity_remove");
		registerItem(new BlockEntityAdderItem(1, false), "server_block_entity_add");
		registerItem(new BlockEntityAdderItem(-1, false), "server_block_entity_remove");

		registerItem(new ChunkAdderItem(1, true), "client_chunk_add");
		registerItem(new ChunkAdderItem(-1, true), "client_chunk_remove");
		registerItem(new ChunkAdderItem(1, false), "server_chunk_add");
		registerItem(new ChunkAdderItem(-1, false), "server_chunk_remove");

		registerItem(new EntityAdderItem(1, true), "client_entity_add");
		registerItem(new EntityAdderItem(-1, true), "client_entity_remove");
		registerItem(new EntityAdderItem(1, false), "server_entity_add");
		registerItem(new EntityAdderItem(-1, false), "server_entity_remove");

		registerItem(new WorldAdderItem(1, true), "client_world_add");
		registerItem(new WorldAdderItem(-1, true), "client_world_remove");
		registerItem(new WorldAdderItem(1, false), "server_world_add");
		registerItem(new WorldAdderItem(-1, false), "server_world_remove");
	}

	private static void registerItem(Item item, String path) {
		Registry.register(Registry.ITEM, id(path), item);
	}
}
