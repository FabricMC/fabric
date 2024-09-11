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

package net.fabricmc.fabric.mixin.entity.event;

import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.entity.Entity;
import net.minecraft.entity.conversion.EntityConversionContext;
import net.minecraft.entity.mob.MobEntity;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;

@Mixin(MobEntity.class)
public class MobEntityMixin {
	@ModifyArg(method = "convertTo(Lnet/minecraft/entity/EntityType;Lnet/minecraft/entity/conversion/EntityConversionContext;Lnet/minecraft/entity/SpawnReason;Lnet/minecraft/entity/conversion/EntityConversionContext$Finalizer;)Lnet/minecraft/entity/mob/MobEntity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
	private Entity afterEntityConverted(Entity converted, @Local(argsOnly = true) EntityConversionContext conversionContext) {
		ServerLivingEntityEvents.MOB_CONVERSION.invoker().onConversion((MobEntity) (Object) this, (MobEntity) converted, conversionContext);
		return converted;
	}
}
