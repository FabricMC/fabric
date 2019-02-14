/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.mixin.dimension;

import net.fabricmc.fabric.impl.dimension.FabricDimensionComponents;
import net.minecraft.entity.Entity;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {

	//This is hooking into the vanilla teleportation logic, but if DEFAULT_TELEPORTER has been set it will be used rather  than the vanilla logic
	@Inject(method = "method_14558", at = @At("HEAD"), cancellable = true)
	private static void method_14558(ServerPlayerEntity entity, DimensionType dimensionType, ServerWorld previousWorld, ServerWorld newWorld, CallbackInfo info) {
		if (FabricDimensionComponents.INSTANCE.NEXT_TELEPORTER != null) {
			FabricDimensionComponents.INSTANCE.NEXT_TELEPORTER.teleport(entity, previousWorld, newWorld);

			newWorld.spawnEntity(entity);
			newWorld.method_8553(entity);
			entity.setWorld(newWorld);

			FabricDimensionComponents.INSTANCE.NEXT_TELEPORTER = null;
			info.cancel();
		}
	}

}
