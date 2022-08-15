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

package net.fabricmc.fabric.mixin.dimension;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.nbt.NbtElement;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.server.Main;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.LevelStorage;

/**
 * This Mixin aims to solve a Minecraft Vanilla bug where datapacks are ignored during creation of the
 * initial LevelProperties when a dedicated server creates a completely new level.
 *
 * <p>This also includes the datapacks of loaded Fabric mods, and results in modded dimensions only
 * being available after restarting the server, once the world has been created.
 *
 * <p>This Mixin aims to solve this problem by saving and loading the level.dat file once, after
 * a new set of level properties is created. This will apply the same logic as reloading the
 * level.dat after a restart, now including all datapack dimensions.
 *
 * <p>See https://bugs.mojang.com/browse/MC-195468 for a related bug report.
 *
 * <p>TODO: Retest if this bug still occurs without this Mixin by launching a dedicated server with the
 * dimension testmod, and no world directory. If the dimension is available (i.e. in /execute in, or via
 * the testmod's commands), then the bug is fixed and this Mixin can be removed.
 */
@Mixin(value = Main.class)
public class MainMixin {
	@Unique
	private static LevelStorage.Session session;

	@Unique
	private static DynamicRegistryManager.Mutable drm;

	@Unique
	private static DynamicOps<NbtElement> ops;

	@ModifyVariable(method = "main", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/storage/LevelStorage;createSession(Ljava/lang/String;)Lnet/minecraft/world/level/storage/LevelStorage$Session;"))
	private static LevelStorage.Session captureSession(LevelStorage.Session value) {
		session = value;
		return value;
	}

	@ModifyVariable(method = "method_43613", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/registry/DynamicRegistryManager;createAndLoad()Lnet/minecraft/util/registry/DynamicRegistryManager$Mutable;"))
	private static DynamicRegistryManager.Mutable captureDrm(DynamicRegistryManager.Mutable value) {
		drm = value;
		return value;
	}

	// The value is stored as DynamicOps instead of RegistryOps in the bytecode
	@ModifyVariable(method = "method_43613", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/dynamic/RegistryOps;ofLoaded(Lcom/mojang/serialization/DynamicOps;Lnet/minecraft/util/registry/DynamicRegistryManager$Mutable;Lnet/minecraft/resource/ResourceManager;)Lnet/minecraft/util/dynamic/RegistryOps;"))
	private static DynamicOps<NbtElement> captureOps(DynamicOps<NbtElement> value) {
		ops = value;
		return value;
	}

	@Redirect(method = "method_43613", at = @At(value = "NEW", target = "net/minecraft/world/level/LevelProperties"))
	private static LevelProperties onCreateNewLevelProperties(LevelInfo levelInfo, GeneratorOptions generatorOptions, Lifecycle lifecycle) {
		DataPackSettings dataPackSettings = levelInfo.getDataPackSettings();

		// Save the level.dat file
		session.backupLevelDataFile(drm, new LevelProperties(levelInfo, generatorOptions, lifecycle));

		// And reload it again, and replace the actual level properties with it
		return (LevelProperties) session.readLevelProperties(ops, dataPackSettings, drm.getRegistryLifecycle());
	}
}
