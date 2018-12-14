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
import net.fabricmc.fabric.block.BreakInteractable;
import net.fabricmc.fabric.events.PlayerInteractionEvent;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class FabricAPIInitializer implements ModInitializer {
	@Override
	public void onInitialize() {
		PlayerInteractionEvent.BREAK_BLOCK.register((player, world, hand, pos, direction) -> {
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
