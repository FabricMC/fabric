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

package net.fabricmc.fabric.mixin.event.interaction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;

import net.fabricmc.fabric.api.entity.FakePlayer;

@Mixin(PlayerAdvancementTracker.class)
public class PlayerAdvancementTrackerMixin {
	@Shadow
	private ServerPlayerEntity owner;

	@Inject(method = "setOwner", at = @At("HEAD"), cancellable = true)
	void preventOwnerOverride(ServerPlayerEntity newOwner, CallbackInfo ci) {
		if (newOwner instanceof FakePlayer) {
			// Prevent fake players with the same UUID as a real player from stealing the real player's advancement tracker.
			ci.cancel();
		}
	}

	@Inject(method = "grantCriterion", at = @At("HEAD"), cancellable = true)
	void preventGrantCriterion(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> ci) {
		if (owner instanceof FakePlayer) {
			// Prevent granting advancements to fake players.
			ci.setReturnValue(false);
		}
	}
}
