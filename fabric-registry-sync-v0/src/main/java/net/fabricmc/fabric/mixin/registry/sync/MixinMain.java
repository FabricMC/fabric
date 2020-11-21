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

package net.fabricmc.fabric.mixin.registry.sync;

import java.io.IOException;
import java.nio.file.Path;

import com.mojang.serialization.DynamicOps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.server.Main;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.nbt.Tag;
import net.minecraft.resource.ResourceManager;

import net.fabricmc.fabric.impl.registry.sync.PersistentDynamicRegistryHandler;

@Mixin(Main.class)
public class MixinMain {
	@Unique
	private static Path fabric_saveDir;

	@Redirect(method = "main", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/LevelStorage;createSession(Ljava/lang/String;)Lnet/minecraft/world/level/storage/LevelStorage$Session;"))
	private static LevelStorage.Session levelStorageCreateSession(LevelStorage levelStorage, String levelName) throws IOException {
		LevelStorage.Session session = levelStorage.createSession(levelName);
		fabric_saveDir = ((AccessorLevelStorageSession) session).getDirectory();
		return session;
	}

	@Redirect(method = "main", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/dynamic/RegistryOps;of(Lcom/mojang/serialization/DynamicOps;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/registry/DynamicRegistryManager$Impl;)Lnet/minecraft/util/dynamic/RegistryOps;"))
	private static RegistryOps<Tag> ofRegistryOps(DynamicOps<Tag> delegate, ResourceManager resourceManager, DynamicRegistryManager.Impl impl) {
		RegistryOps<Tag> registryOps = RegistryOps.of(delegate, resourceManager, impl);
		PersistentDynamicRegistryHandler.remapDynamicRegistries(impl, fabric_saveDir);
		return registryOps;
	}
}
