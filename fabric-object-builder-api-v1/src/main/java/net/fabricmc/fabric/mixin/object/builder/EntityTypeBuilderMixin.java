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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.types.Type;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.fabricmc.fabric.impl.object.builder.FabricEntityTypeImpl;

@Mixin(EntityType.Builder.class)
public abstract class EntityTypeBuilderMixin<T extends Entity> implements FabricEntityType.Builder<T> {
	@Unique
	private Boolean alwaysUpdateVelocity = null;

	@Override
	public EntityType.Builder<T> alwaysUpdateVelocity(boolean forceTrackedVelocityUpdates) {
		alwaysUpdateVelocity = forceTrackedVelocityUpdates;
		return (EntityType.Builder<T>) (Object) this;
	}

	@Inject(method = "build", at = @At("RETURN"))
	public void build(String id, CallbackInfoReturnable<EntityType<T>> cir) {
		if (!(cir.getReturnValue() instanceof FabricEntityTypeImpl entityType)) {
			throw new IllegalStateException();
		}

		entityType.fabric_setAlwaysUpdateVelocity(alwaysUpdateVelocity);
	}

	@WrapOperation(method = "build", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;getChoiceType(Lcom/mojang/datafixers/DSL$TypeReference;Ljava/lang/String;)Lcom/mojang/datafixers/types/Type;"))
	private @Nullable Type<?> allowNullId(DSL.TypeReference typeReference, String id, Operation<Type<?>> original) {
		if (id == null) {
			return null;
		}

		return original.call(typeReference, id);
	}
}
