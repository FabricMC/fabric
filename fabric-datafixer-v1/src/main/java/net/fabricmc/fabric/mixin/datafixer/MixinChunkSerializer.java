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

package net.fabricmc.fabric.mixin.datafixer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ChunkSerializer;

import net.fabricmc.fabric.impl.datafixer.FabricDataFixesImpl;

@Mixin(ChunkSerializer.class)
public abstract class MixinChunkSerializer {
	@ModifyVariable(at = @At(value = "INVOKE", target = "net/minecraft/nbt/CompoundTag.putInt(Ljava/lang/String;I)V", ordinal = 0), method = "serialize(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/Chunk;)Lnet/minecraft/nbt/CompoundTag;", name = "compoundTag")
	private static CompoundTag fabric_addModDataVersions(CompoundTag input) {
		FabricDataFixesImpl.INSTANCE.addFixerVersions(input);
		return input;
	}
}
