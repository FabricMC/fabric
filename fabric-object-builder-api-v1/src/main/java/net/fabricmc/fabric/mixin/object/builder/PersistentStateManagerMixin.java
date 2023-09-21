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

package net.fabricmc.fabric.mixin.object.builder;

import java.io.File;
import java.io.FileInputStream;
import java.io.PushbackInputStream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PersistentStateManager;

@Mixin(PersistentStateManager.class)
class PersistentStateManagerMixin {
	/**
	 * Handle mods passing a null DataFixTypes to a PersistentState.Type.
	 */
	@Inject(method = "readNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtHelper;getDataVersion(Lnet/minecraft/nbt/NbtCompound;I)I"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private void handleNullDataFixType(String id, DataFixTypes dataFixTypes, int currentSaveVersion, CallbackInfoReturnable<NbtCompound> cir, File file, FileInputStream fileInputStream, PushbackInputStream pushbackInputStream, NbtCompound nbtCompound) {
		if (dataFixTypes == null) {
			cir.setReturnValue(nbtCompound);
		}
	}
}
