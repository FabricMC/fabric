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

package net.fabricmc.fabric.mixin.entity;

import net.fabricmc.fabric.entity.EntityTrackingRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.EntityTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityTracker.class)
public abstract class MixinEntityTracker {
	@Shadow
	public abstract void add(Entity var1, int var2, int var3, boolean var4);

	@Inject(at = @At("HEAD"), method = "add", cancellable = true)
	public void add(Entity entity, CallbackInfo info) {
		if (entity != null) {
			EntityTrackingRegistry.Entry entry = EntityTrackingRegistry.INSTANCE.get(entity.getType());
			if (entry != null) {
				add(entity, entry.getTrackingDistance(), entry.getUpdateIntervalTicks(), entry.alwaysUpdateVelocity());
				info.cancel();
			}
		}
	}
}
