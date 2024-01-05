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

package net.fabricmc.fabric.mixin.gametest;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.server.command.TestCommand;

@Mixin(TestCommand.class)
public class TestCommandMixin {
	@Unique
	private static final String OUTPUT_DIR = System.getProperty("fabric-api.gametest.structures.output-dir");

	@ModifyArg(
			method = "executeExport(Lnet/minecraft/server/command/ServerCommandSource;Ljava/lang/String;)I",
			at = @At(
					value = "INVOKE",
					target = "Ljava/nio/file/Paths;get(Ljava/lang/String;[Ljava/lang/String;)Ljava/nio/file/Path;"
			)
	)
	private static String useCustomOutputDirectory(String first) {
		if (OUTPUT_DIR != null) {
			return OUTPUT_DIR;
		}

		return first;
	}
}
