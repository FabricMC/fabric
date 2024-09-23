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

package net.fabricmc.fabric.api.components.v1.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;

public final class ComponentEvents {
	public static final TargetedEvent<Entity, ServerWorld> ENTITY_LOAD = TargetedEvent.create(ServerEntityEvents.ENTITY_LOAD,
			event -> event::accept);

	public static final TargetedEvent<Entity, ServerWorld> ENTITY_UNLOAD = TargetedEvent.create(ServerEntityEvents.ENTITY_UNLOAD,
			event -> event::accept);

	public static final TargetedEvent<LivingEntity, EquipmentChangeEvent> EQUIPMENT_CHANGE = TargetedEvent.create(ServerEntityEvents.EQUIPMENT_CHANGE,
			event -> (livingEntity, equipmentSlot, previousStack, currentStack) ->
					event.accept(livingEntity, new EquipmentChangeEvent(equipmentSlot, previousStack, currentStack)));

	private ComponentEvents() {

	}

	public record EquipmentChangeEvent(EquipmentSlot equipmentSlot, ItemStack previousStack, ItemStack currentStack) {

	}
}
