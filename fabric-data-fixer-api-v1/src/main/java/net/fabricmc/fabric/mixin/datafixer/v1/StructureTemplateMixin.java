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

package net.fabricmc.fabric.mixin.datafixer.v1;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StructureTemplate;

import net.fabricmc.fabric.impl.datafixer.v1.FabricDataFixesInternals;

@Mixin(StructureTemplate.class)
public abstract class StructureTemplateMixin {
	@Inject(method = "writeNbt", at = @At("RETURN"))
	private void addModDataVersions(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
		FabricDataFixesInternals.get().addModDataVersions(nbt);
	}
}
