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

package net.fabricmc.fabric.mixin.interaction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;

import net.fabricmc.fabric.api.interaction.v1.event.player.ServerPlayerEntityAttackEvents;

@Mixin(ServerPlayerEntity.class)
abstract class ServerPlayerEntityMixin {
	@Shadow
	public abstract ServerWorld getServerWorld();

	@Inject(method = "attack", at = @At("HEAD"), cancellable = true)
	private void handleAttackEntity(Entity target, CallbackInfo info) {
		if (!ServerPlayerEntityAttackEvents.ALLOW.invoker().allowEntityAttack((ServerPlayerEntity) (Object) this, this.getServerWorld(), Hand.MAIN_HAND, target)) {
			info.cancel();
			// TODO: Cancel needed?
			return;
		}

		ServerPlayerEntityAttackEvents.BEFORE.invoker().beforeEntityAttack((ServerPlayerEntity) (Object) this, this.getServerWorld(), Hand.MAIN_HAND, target);
	}

	@Inject(method = "attack", at = @At("RETURN"))
	private void handleAfterAttackEntity(Entity target, CallbackInfo info) {
		ServerPlayerEntityAttackEvents.AFTER.invoker().afterEntityAttack((ServerPlayerEntity) (Object) this, this.getServerWorld(), Hand.MAIN_HAND, target);
	}
}
