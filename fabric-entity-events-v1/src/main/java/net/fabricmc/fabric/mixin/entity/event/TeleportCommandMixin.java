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

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.server.command.TeleportCommand;

@Mixin(TeleportCommand.class)
abstract class TeleportCommandMixin {
	/**
	 * We need to fire the change world event for entities that are teleported using the `/teleport` command.
	 */
	// TODO 1.19.4 checkme
//	@Inject(method = "teleport", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setRemoved(Lnet/minecraft/entity/Entity$RemovalReason;)V"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
//	private static void afterEntityTeleportedToWorld(ServerCommandSource source, Entity originalEntity, ServerWorld destination, double x, double y, double z, Set<PlayerPositionLookS2CPacket.Flag> movementFlags, float yaw, float pitch, @Coerce Object facingLocation, CallbackInfo ci, float clampedYaw, float clampedPitch, float h, Entity newEntity) {
//		ServerEntityWorldChangeEvents.AFTER_ENTITY_CHANGE_WORLD.invoker().afterChangeWorld(originalEntity, newEntity, ((ServerWorld) originalEntity.world), destination);
//	}
}
