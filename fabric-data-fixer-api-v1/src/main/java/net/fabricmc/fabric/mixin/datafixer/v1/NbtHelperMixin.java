/*
 * Copyright 2022 QuiltMC
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

package net.fabricmc.fabric.mixin.datafixer.v1;

import com.mojang.datafixers.DataFixer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;

import net.fabricmc.fabric.impl.datafixer.v1.QuiltDataFixesInternals;

@Mixin(NbtHelper.class)
public abstract class NbtHelperMixin {
	@Inject(
			method = "update(Lcom/mojang/datafixers/DataFixer;Lnet/minecraft/datafixer/DataFixTypes;Lnet/minecraft/nbt/NbtCompound;II)Lnet/minecraft/nbt/NbtCompound;",
			at = @At("RETURN"),
			cancellable = true
	)
	private static void updateDataWithFixers(DataFixer fixer, DataFixTypes fixTypes, NbtCompound compound,
											 int oldVersion, int targetVersion, CallbackInfoReturnable<NbtCompound> cir) {
		cir.setReturnValue(QuiltDataFixesInternals.get().updateWithAllFixers(fixTypes, cir.getReturnValue()));
	}
}
