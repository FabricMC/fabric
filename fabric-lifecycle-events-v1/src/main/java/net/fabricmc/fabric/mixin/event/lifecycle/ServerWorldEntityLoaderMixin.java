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

package net.fabricmc.fabric.mixin.event.lifecycle;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;

@Mixin(targets = "net/minecraft/server/world/ServerWorld$class_5526")
abstract class ServerWorldEntityLoaderMixin {
	// final synthetic Lnet/minecraft/server/world/ServerWorld; field_26936
	@SuppressWarnings("ShadowTarget")
	@Shadow
	@Final
	private ServerWorld field_26936;

	// onLoadEntity
	@Inject(method = "method_31798(Lnet/minecraft/entity/Entity;)V", at = @At("TAIL"))
	private void invokeEntityLoadEvent(Entity entity, CallbackInfo ci) {
		ServerEntityEvents.ENTITY_LOAD.invoker().onLoad(entity, this.field_26936);
	}
}
