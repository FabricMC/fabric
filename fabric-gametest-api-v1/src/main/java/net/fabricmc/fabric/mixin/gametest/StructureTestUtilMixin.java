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

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.resource.Resource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.test.StructureTestUtil;
import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

@Mixin(StructureTestUtil.class)
public abstract class StructureTestUtilMixin {
	private static final String GAMETEST_STRUCTURE_PATH = "gametest/structures/";

	// Use "gametest/structures/" as default test structure directory for ".snbt" files. Fall back to vanilla if not found.
	@Inject(at = @At("HEAD"), method = "createStructure(Ljava/lang/String;Lnet/minecraft/server/world/ServerWorld;)Lnet/minecraft/structure/Structure;", cancellable = true)
	private static void createStructure(String id, ServerWorld world, CallbackInfoReturnable<Structure> cir) {
		Identifier baseId = new Identifier(id);
		Identifier structureId = new Identifier(baseId.getNamespace(), GAMETEST_STRUCTURE_PATH + baseId.getPath() + ".snbt");

		String snbt = null;
		try {
			Resource resource = world.getServer().getResourceManager().getResource(structureId);
			try (InputStream inputStream = resource.getInputStream()) {
				snbt = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
			}
		} catch (IOException ignore) {
		}

		if (snbt != null) {
			try {
				NbtCompound nbtCompound = NbtHelper.fromNbtProviderString(snbt);
				Structure structure = world.getStructureManager().createStructure(nbtCompound);
				cir.setReturnValue(structure);
			} catch (CommandSyntaxException e) {
				throw new RuntimeException("Error while trying to load structure: " + structureId, e);
			}
		}
	}

	// If not in "gametest/structures/" or world structures, try the test structure directory.
	// Adds "gametest/structures/" to the error message for a better overview.
	@Redirect(method = "createStructure(Ljava/lang/String;Lnet/minecraft/server/world/ServerWorld;)Lnet/minecraft/structure/Structure;",
			at = @At(value = "INVOKE", target = "Ljava/nio/file/Paths;get(Ljava/lang/String;[Ljava/lang/String;)Ljava/nio/file/Path;"))
	private static Path handlePostCreateStructure(String testStructuresDirectoryName, String[] more, String structureId) throws FileNotFoundException {
		// createStructure() calls Paths.get() with only the structure id, so technically we should be safe.
		if (more.length != 1) {
			return Paths.get(testStructuresDirectoryName, more);
		}

		more[0] = more[0].replace(':', '/'); // fix path, when it contains a mod id
		Path path = Paths.get(testStructuresDirectoryName, more);
		if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
			throw new FileNotFoundException("Could not find structure '" + structureId + "' in '" + GAMETEST_STRUCTURE_PATH + "' or '" + testStructuresDirectoryName + "' and is not available in the world structures either.");
		}
		return path;
	}
}
