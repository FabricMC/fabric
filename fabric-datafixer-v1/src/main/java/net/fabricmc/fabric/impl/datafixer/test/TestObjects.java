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

package net.fabricmc.fabric.impl.datafixer.test;

import java.util.Optional;

import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;

import net.minecraft.util.SystemUtil;

import net.fabricmc.fabric.api.datafixer.v1.DataFixerHelper;
import net.fabricmc.fabric.api.datafixer.v1.FabricSchemas;
import net.fabricmc.fabric.api.datafixer.v1.SimpleFixes;

public class TestObjects {
	public static final int VERSION = 3;

	public static void create() {
		DataFixerBuilder builder = new DataFixerBuilder(VERSION);
		builder.addSchema(0, FabricSchemas.FABRIC_SCHEMA);
		Schema schema_1 = builder.addSchema(1, FabricSchemas.IDENTIFIER_NORMALIZE_SCHEMA);
		SimpleFixes.INSTANCE.addBlockRenameFix(builder, "rename test", "test:oldblock", "test:newblock", schema_1);

		Schema schema_2 = builder.addSchema(2, FabricSchemas.IDENTIFIER_NORMALIZE_SCHEMA);

		SimpleFixes.INSTANCE.addBlockEntityTransformFix(builder, "Ground beef", "test:testblockentity", (dynamic) -> {
			Optional<Number> optional = dynamic.get("ground_beef").asNumber();

			if (optional.isPresent()) {
				if (optional.get().intValue() == 0) {
					return dynamic.set("ground_beef", dynamic.createInt(509));
				}
			}

			return dynamic;
		}, schema_2);

		Schema schema_3 = builder.addSchema(3, FabricSchemas.IDENTIFIER_NORMALIZE_SCHEMA);
		SimpleFixes.INSTANCE.addBlockEntityRenameFix(builder, "Rename test BE", "test:testblockentity", "test:testblockentity2", schema_3); // This works

		DataFixerHelper.INSTANCE.registerFixer("fabric:datafixer", VERSION, builder.build(SystemUtil.getServerWorkerExecutor()));
	}
}
