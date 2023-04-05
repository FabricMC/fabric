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

package net.fabricmc.fabric.api.event.client.player;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.ActionResult;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface ClientPreAttackCallback {
	Event<ClientPreAttackCallback> EVENT = EventFactory.createArrayBacked(
			ClientPreAttackCallback.class,
			(listeners) -> (player) -> {
				for (ClientPreAttackCallback event : listeners) {
					ActionResult result = event.onClientPlayerPreAttack(player);

					if (result != ActionResult.PASS) {
						return result;
					}
				}
				return ActionResult.PASS;
			}
	);

	ActionResult onClientPlayerPreAttack(ClientPlayerEntity player);
}
