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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.resource.Resource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.test.StructureTestUtil;
import net.minecraft.util.Identifier;

@Mixin(StructureTestUtil.class)
public abstract class StructureTestUtilMixin {
	private static final String GAMETEST_STRUCTURE_PATH = "gametest/structures/";

	// Replace the default test structure loading with something that works a bit better for mods.
	@Inject(at = @At("HEAD"), method = "createStructureTemplate(Ljava/lang/String;Lnet/minecraft/server/world/ServerWorld;)Lnet/minecraft/structure/StructureTemplate;", cancellable = true)
	private static void createStructure(String id, ServerWorld world, CallbackInfoReturnable<StructureTemplate> cir) {
		Identifier baseId = new Identifier(id);
		Identifier structureId = new Identifier(baseId.getNamespace(), GAMETEST_STRUCTURE_PATH + baseId.getPath() + ".snbt");

		try {
			Resource resource = world.getServer().getResourceManager().getResource(structureId).orElse(null);

			if (resource == null) {
				throw new RuntimeException("Unable to get resource: " + structureId);
			}

			String snbt;

			try (InputStream inputStream = resource.getInputStream()) {
				snbt = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
			}

			NbtCompound nbtCompound = NbtHelper.fromNbtProviderString(snbt);
			StructureTemplate structure = world.getStructureTemplateManager().createTemplate(nbtCompound);

			cir.setReturnValue(structure);
		} catch (IOException | CommandSyntaxException e) {
			throw new RuntimeException("Error while trying to load structure: " + structureId, e);
		}
	}
}
