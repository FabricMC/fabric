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

package net.fabricmc.fabric.api.event.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

/**
 * Callback for left-clicking ("attacking") an entity.
 *
 * Upon return:
 * - SUCCESS cancels further processing and, on the client, sends a packet to the server.
 * - PASS falls back to further processing.
 * - FAIL cancels further processing and does not send a packet to the server.
 */
public interface AttackEntityCallback {
	public static final Event<AttackEntityCallback> EVENT = EventFactory.arrayBacked(AttackEntityCallback.class,
		(listeners) -> (player, world, hand, entity, hitResult) -> {
			for (AttackEntityCallback event : listeners) {
				ActionResult result = event.interact(player, world, hand, entity, hitResult);
				if (result != ActionResult.PASS) {
					return result;
				}
			}

			return ActionResult.PASS;
		}
	);

	ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, /* Nullable */ EntityHitResult hitResult);
}
