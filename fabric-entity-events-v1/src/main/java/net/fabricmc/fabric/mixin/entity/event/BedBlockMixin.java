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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BedBlock;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(BedBlock.class)
abstract class BedBlockMixin {
	// Synthetic lambda body for Either.ifLeft in onUse
	@Inject(method = "method_19283", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;sendMessage(Lnet/minecraft/text/Text;Z)V"), cancellable = true)
	private static void onOnUse(PlayerEntity player, PlayerEntity.SleepFailureReason reason, CallbackInfo info) {
		// EntitySleepEvents.ALLOW_SLEEPING allows modders to return SleepFailureReason instances
		// with a null message, which vanilla's code doesn't guard against. This prevents a (luckily caught) NPE
		// when a failure reason like that is returned from the event.
		// The NPE can also be reproduced in vanilla with custom data pack dimensions (MC-235035, which is also fixed here).
		if (reason.toText() == null) {
			info.cancel();
		}
	}
}
