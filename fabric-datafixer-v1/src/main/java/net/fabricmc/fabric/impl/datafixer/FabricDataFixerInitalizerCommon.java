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

package net.fabricmc.fabric.impl.datafixer;

import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.datafixer.v1.DataFixerEntrypoint;
import net.fabricmc.fabric.api.datafixer.v1.DataFixerHelper;
import net.fabricmc.fabric.api.datafixer.v1.FabricSchemas;
import net.fabricmc.fabric.api.datafixer.v1.SimpleFixes;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class FabricDataFixerInitalizerCommon implements ModInitializer {

	@Override
	public void onInitialize() {
		// TODO Add logic in both Common and Client Initializer to lock DataFixer registration once Client loads or Server starts. Maybe after FabricLoader Entrypoint finishes.

		// Ignore test blocks for logic:

		//Registry.register(Registry.BLOCK, new Identifier("test:testo"), new Block(FabricBlockSettings.of(Material.CLAY).build())); // For data version 1 // Undefined

		Registry.register(Registry.BLOCK, new Identifier("test:test_block"), new Block(FabricBlockSettings.of(Material.CLAY).build())); // For data version 2

		// Test DataFixer, will remove later

		DataFixerBuilder builder = new DataFixerBuilder(TEST_DATA_VERSION);
		
		builder.addSchema(0, FabricSchemas.FABRIC_SCHEMA); // This is here to register all the TypeReferences into the DataFixer

		Schema v1 = builder.addSchema(1, FabricSchemas.IDENTIFIER_NORMALIZE_SCHEMA);

		SimpleFixes.INSTANCE.addBlockRenameFix(builder, "rename testp to test_block", "test:testo", "test:test_block", v1);

		SimpleFixes.INSTANCE.addEntityTransformFix(builder, "addRandomAttribute", (name, dynamic) -> { // Doesn't actually show up in NBT data unless you mixin to entities.
			return Pair.of(name, dynamic.set("rna", dynamic.createInt(509)));
		}, v1);

		DataFixerHelper.INSTANCE.registerFixer("fabric_test", TEST_DATA_VERSION, builder.build(SystemUtil.getServerWorkerExecutor()));
		
		// End of testing logic

		// Once the server has started, we need to stop registering DataFixers.
		ServerStartCallback.EVENT.register(server -> FabricDataFixerImpl.INSTANCE.lock());
	}

	public static int TEST_DATA_VERSION = 1;

}
