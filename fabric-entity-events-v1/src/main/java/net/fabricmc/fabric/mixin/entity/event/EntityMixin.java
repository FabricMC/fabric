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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;

@Mixin(Entity.class)
abstract class EntityMixin {
	@Shadow
	public World world;

	@Inject(method = "moveToWorld", at = @At("RETURN"))
	private void afterWorldChanged(ServerWorld destination, CallbackInfoReturnable<Entity> cir) {
		// Ret will only have an entity if the teleport worked (entity not removed, teleportTarget was valid, entity was successfully created)
		Entity ret = cir.getReturnValue();

		if (ret != null) {
			ServerEntityWorldChangeEvents.AFTER_ENTITY_CHANGE_WORLD.invoker().afterChangeWorld((Entity) (Object) this, ret, (ServerWorld) this.world, (ServerWorld) ret.world);
		}
	}
}
