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

package net.fabricmc.fabric.mixin.registry.sync.client;

import java.nio.file.Path;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.level.storage.LevelStorage;

import net.fabricmc.fabric.impl.client.registry.sync.RegistryRemovalChecker;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin {
	@Inject(method = "createSession", at = @At(value = "RETURN"))
	private void createInitialRegistryFile(CallbackInfoReturnable<Optional<LevelStorage.Session>> cir) {
		// There are multiple returns in this method. We want to target the first two.
		// Since specifying 2 ordinals is impossible, just check whether the return value is what we want.
		cir.getReturnValue().ifPresent(session -> {
			Path jsonFile = session.getDirectory(WorldSavePath.ROOT).resolve(RegistryRemovalChecker.FILE_NAME);
			RegistryRemovalChecker.write(jsonFile);
		});
	}
}
