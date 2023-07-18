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

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.BeehiveBlock;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(BeehiveBlock.class)
class BeehiveBlockMixin {
	@Inject(
			method = "angerNearbyBees",
			cancellable = true,
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Lnet/minecraft/world/World;getNonSpectatingEntities(Ljava/lang/Class;Lnet/minecraft/util/math/Box;)Ljava/util/List;",
					ordinal = 1 // Only capture the PlayerEntity call.
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void afterNearbyBeesPlayers(World world, BlockPos pos, CallbackInfo ci, List<BeeEntity> bees, List<PlayerEntity> players) {
		// If a fake player broke the beehive, there will be no nearby players. This causes a crash later on as we try
		// to pick a random player - we early return to avoid this.
		if (players.isEmpty()) ci.cancel();
	}
}
