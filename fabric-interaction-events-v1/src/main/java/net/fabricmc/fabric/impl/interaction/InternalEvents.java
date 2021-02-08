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

package net.fabricmc.fabric.impl.interaction;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Internal event listeners for use to reimplement parts of {@code fabric-events-interaction-v0}.
 */
@ApiStatus.Internal
public final class InternalEvents {
	public static final Event<BetweenBlockCancelAndBreak> BETWEEN_BLOCK_CANCEL_AND_BREAK = EventFactory.createArrayBacked(BetweenBlockCancelAndBreak.class, callbacks -> (world, player, pos, state, blockEntity) -> {
		for (BetweenBlockCancelAndBreak callback : callbacks) {
			if (!callback.betweenBlockCancelAndBreak(world, player, pos, state, blockEntity)) {
				return false;
			}
		}

		return true;
	});

	private InternalEvents() {
	}

	@FunctionalInterface
	public interface BetweenBlockCancelAndBreak {
		boolean betweenBlockCancelAndBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity);
	}
}
