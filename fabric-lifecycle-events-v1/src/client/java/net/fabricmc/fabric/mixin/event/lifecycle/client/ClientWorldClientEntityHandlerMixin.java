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

package net.fabricmc.fabric.mixin.event.lifecycle.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;

@Mixin(targets = "net/minecraft/client/world/ClientWorld$ClientEntityHandler")
abstract class ClientWorldClientEntityHandlerMixin {
	// final synthetic Lnet/minecraft/client/world/ClientWorld; field_27735
	@SuppressWarnings("ShadowTarget")
	@Shadow
	@Final
	private ClientWorld field_27735;

	// Call our load event after vanilla has loaded the entity
	@Inject(method = "startTracking(Lnet/minecraft/entity/Entity;)V", at = @At("TAIL"))
	private void invokeLoadEntity(Entity entity, CallbackInfo ci) {
		ClientEntityEvents.ENTITY_LOAD.invoker().onLoad(entity, this.field_27735);
	}

	// Call our unload event before vanilla does.
	@Inject(method = "stopTracking(Lnet/minecraft/entity/Entity;)V", at = @At("HEAD"))
	private void invokeUnloadEntity(Entity entity, CallbackInfo ci) {
		ClientEntityEvents.ENTITY_UNLOAD.invoker().onUnload(entity, this.field_27735);
	}
}
