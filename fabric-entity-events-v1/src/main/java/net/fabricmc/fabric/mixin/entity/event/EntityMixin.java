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

import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;

@Mixin(Entity.class)
abstract class EntityMixin {
	@Shadow
	public World world;

	@Inject(method = "moveToWorld", at = @At("RETURN"))
	private void afterWorldChanged(Entity.class_9776 arg, CallbackInfoReturnable<Entity> cir) {
		// Ret will only have an entity if the teleport worked (entity not removed, teleportTarget was valid, entity was successfully created)
		Entity ret = cir.getReturnValue();

		if (ret != null) {
			ServerEntityWorldChangeEvents.AFTER_ENTITY_CHANGE_WORLD.invoker().afterChangeWorld((Entity) (Object) this, ret, (ServerWorld) this.world, (ServerWorld) ret.getWorld());
		}
	}

	/**
	 * We need to fire the change world event for entities that are teleported using the `/teleport` command.
	 */
	@Inject(method = "teleport(Lnet/minecraft/server/world/ServerWorld;DDDLjava/util/Set;FF)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setRemoved(Lnet/minecraft/entity/Entity$RemovalReason;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void afterEntityTeleportedToWorld(ServerWorld destination, double x, double y, double z, Set<PositionFlag> flags, float yaw, float pitch, CallbackInfoReturnable<Boolean> cir, float i, Entity newEntity) {
		Entity originalEntity = (Entity) (Object) this;
		ServerEntityWorldChangeEvents.AFTER_ENTITY_CHANGE_WORLD.invoker().afterChangeWorld(originalEntity, newEntity, ((ServerWorld) originalEntity.getWorld()), destination);
	}
}
