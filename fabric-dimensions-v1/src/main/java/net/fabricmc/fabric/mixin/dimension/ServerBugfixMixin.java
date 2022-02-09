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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.server.Main;
import net.minecraft.util.registry.DynamicRegistryManager;
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
 * <p>In 1.18: Retest if this bug still occurs without this Mixin by launching a dedicated server with the
 * dimension testmod, and no world directory. If the dimension is available (i.e. in /execute in, or via
 * the testmod's commands), then the bug is fixed and this Mixin can be removed.
 */
@Mixin(value = Main.class)
public class ServerBugfixMixin {
	@Unique
	private static LevelStorage.Session fabric_session;

	@Unique
	private static DynamicRegistryManager fabric_dynamicRegistry;

	// TODO fix me 22w06a
//	@Unique
//	private static RegistryOps<NbtElement> fabric_registryOps;
//
//	@ModifyVariable(at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/registry/DynamicRegistryManager;create()Lnet/minecraft/util/registry/DynamicRegistryManager$Impl;"), method = "main", allow = 1)
//	private static DynamicRegistryManager.Impl captureDynamicRegistry(DynamicRegistryManager value) {
//		fabric_dynamicRegistry = value;
//		return value;
//	}
//
//	@ModifyVariable(at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/level/storage/LevelStorage;createSession(Ljava/lang/String;)Lnet/minecraft/world/level/storage/LevelStorage$Session;"), method = "main", allow = 1)
//	private static LevelStorage.Session captureSession(LevelStorage.Session value) {
//		fabric_session = value;
//		return value;
//	}
//
//	@ModifyVariable(at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/dynamic/RegistryOps;ofLoaded(Lcom/mojang/serialization/DynamicOps;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/registry/DynamicRegistryManager;)Lnet/minecraft/util/dynamic/RegistryOps;"), method = "main", allow = 1)
//	private static RegistryOps<NbtElement> captureRegistryOps(RegistryOps<NbtElement> value) {
//		fabric_registryOps = value;
//		return value;
//	}
//
//	@Redirect(method = "main", at = @At(value = "NEW", target = "net/minecraft/world/level/LevelProperties"), allow = 1)
//	private static LevelProperties onCreateNewLevelProperties(LevelInfo levelInfo, GeneratorOptions generatorOptions, Lifecycle lifecycle) {
//		DataPackSettings dataPackSettings = levelInfo.getDataPackSettings();
//
//		// Save the level.dat file
//		fabric_session.backupLevelDataFile(fabric_dynamicRegistry, new LevelProperties(levelInfo, generatorOptions, lifecycle));
//
//		// And reload it again, and replace the actual level properties with it
//		return (LevelProperties) fabric_session.readLevelProperties(fabric_registryOps, dataPackSettings);
//	}
}
