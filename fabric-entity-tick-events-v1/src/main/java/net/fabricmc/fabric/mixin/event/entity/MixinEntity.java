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

package net.fabricmc.fabric.mixin.event.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.v1.EntityTickCallback;
import net.fabricmc.fabric.impl.event.entity.EntityCascadingEventBridge;
import net.fabricmc.fabric.impl.event.entity.TickEventInternals;

@Mixin(Entity.class)
public abstract class MixinEntity implements EntityCascadingEventBridge {
	private Event<EntityTickCallback<Entity>> fabric_tickEvent;

	@Inject(at = @At("TAIL"), method = "<init>")
	private void fabric_initTickEvent(EntityType<?> type, World world, CallbackInfo ci) {
		this.fabric_tickEvent = TickEventInternals.getOrCreateEntityEvent((Class<Entity>) ((Entity) (Object) this).getClass());
	}

	@Override
	public Event<EntityTickCallback<Entity>> getTickEvent() {
		return this.fabric_tickEvent;
	}
}
