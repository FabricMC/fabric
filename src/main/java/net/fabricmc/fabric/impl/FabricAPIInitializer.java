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

package net.fabricmc.fabric.impl;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.PlayerInteractionEvents;
import net.fabricmc.fabric.api.event.listener.ListenerTypeFactory;
import net.fabricmc.fabric.api.event.callbacks.PlayerInteractCallback;
import net.fabricmc.fabric.block.BreakInteractable;
import net.minecraft.block.BlockState;
import net.minecraft.util.ActionResult;

public class FabricAPIInitializer implements ModInitializer {
	@Override
	public void onInitialize() {
		ListenerTypeFactory.INSTANCE.registerListenerClass(PlayerInteractCallback.class,
			(player, world, hand, pos, direction) -> ActionResult.PASS,
			(listeners) -> (player, world, hand, pos, direction) -> {
			for (PlayerInteractCallback event : listeners) {
				ActionResult result = event.interact(player, world, hand, pos, direction);
				if (result != ActionResult.PASS) {
					return result;
				}
			}

			return ActionResult.PASS;
		});

		PlayerInteractionEvents.ATTACK_BLOCK.register((player, world, hand, pos, direction) -> {
			System.out.println("--- DEMO --- PlayerInteractCallback called!");

			BlockState state = world.getBlockState(pos);
			if (state instanceof BreakInteractable) {
				if (((BreakInteractable) state).onBreakInteract(state, world, pos, player, hand, direction)) {
					return ActionResult.FAILURE;
				}
			} else if (state.getBlock() instanceof BreakInteractable) {
				if (((BreakInteractable) state.getBlock()).onBreakInteract(state, world, pos, player, hand, direction)) {
					return ActionResult.FAILURE;
				}
			}

			return ActionResult.PASS;
		});
	}
}
