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

import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import net.fabricmc.fabric.api.datafixer.v1.DataFixerHelper;
import net.fabricmc.fabric.api.datafixer.v1.FabricSchemas;
import net.fabricmc.fabric.api.datafixer.v1.SimpleFixes;
import net.minecraft.util.SystemUtil;

public class TestObjects {

	private static final int VERSION = 1;

	public static void create() {
		DataFixerBuilder builder = new DataFixerBuilder(VERSION);
		builder.addSchema(0, FabricSchemas.FABRIC_SCHEMA);
		Schema schema_1 = builder.addSchema(1, FabricSchemas.IDENTIFIER_NORMALIZE_SCHEMA);
		SimpleFixes.INSTANCE.addBlockRenameFix(builder, "rename test", "test:oldblock", "test:newblock", schema_1);

		DataFixerHelper.INSTANCE.registerFixer("fabric:datafixer", VERSION, builder.build(SystemUtil.getServerWorkerExecutor()));
	}
}
