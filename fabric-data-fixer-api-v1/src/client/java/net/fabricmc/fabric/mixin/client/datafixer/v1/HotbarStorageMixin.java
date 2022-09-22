/*
 * Copyright (c) 2016-2022 FabricMC
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
 *
 * This file is a modified version of Quilt Standard Libraries,
 * authored by QuiltMC.
 */

package net.fabricmc.fabric.mixin.client.datafixer.v1;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.option.HotbarStorage;
import net.minecraft.nbt.NbtCompound;

import net.fabricmc.fabric.impl.datafixer.v1.FabricDataFixesInternals;

@Mixin(HotbarStorage.class)
public abstract class HotbarStorageMixin {
	@Inject(
			method = "save",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtIo;write(Lnet/minecraft/nbt/NbtCompound;Ljava/io/File;)V"),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void addModDataVersions(CallbackInfo ci, NbtCompound nbt) {
		FabricDataFixesInternals.get().addModDataVersions(nbt);
	}
}
