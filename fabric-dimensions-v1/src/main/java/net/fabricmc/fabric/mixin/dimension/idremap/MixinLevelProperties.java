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

package net.fabricmc.fabric.mixin.dimension.idremap;

import com.mojang.datafixers.DataFixer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelProperties;

import net.fabricmc.fabric.impl.dimension.DimensionIdsFixer;
import net.fabricmc.fabric.impl.dimension.DimensionIdsHolder;
import net.fabricmc.fabric.impl.dimension.DimensionRemapException;
import net.fabricmc.fabric.impl.registry.sync.RemapException;

@Mixin(LevelProperties.class)
public abstract class MixinLevelProperties implements DimensionIdsHolder {
	@Unique
	private CompoundTag fabricDimensionIds = new CompoundTag();

	@Override
	public CompoundTag fabric_getDimensionIds() {
		return fabricDimensionIds;
	}

	@Inject(method = "<init>(Lnet/minecraft/nbt/CompoundTag;Lcom/mojang/datafixers/DataFixer;ILnet/minecraft/nbt/CompoundTag;)V", at = @At("RETURN"))
	private void readDimensionIds(CompoundTag data, DataFixer fixer, int version, CompoundTag player, CallbackInfo ci) {
		CompoundTag savedIds = data.getCompound("fabric_DimensionIds");

		try {
			this.fabricDimensionIds = DimensionIdsFixer.apply(savedIds);
		} catch (RemapException e) {
			throw new DimensionRemapException("Failed to assign unique dimension ids!", e);
		}
	}

	@Inject(method = "updateProperties", at = @At("RETURN"))
	private void writeDimensionIds(CompoundTag data, CompoundTag player, CallbackInfo ci) {
		data.put("fabric_DimensionIds", fabricDimensionIds);
	}
}
