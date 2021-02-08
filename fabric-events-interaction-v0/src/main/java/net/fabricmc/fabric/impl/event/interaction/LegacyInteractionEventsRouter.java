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

package net.fabricmc.fabric.impl.event.interaction;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.interaction.v1.event.player.ServerPlayerBlockBreakEvents;
import net.fabricmc.fabric.impl.interaction.InternalEvents;

public final class LegacyInteractionEventsRouter implements ModInitializer {
	@Override
	public void onInitialize() {
		// Must be lambdas in order to allow for proper invalidation of events to work

		// Cannot use ServerPlayerBreakBlockEvents.ALLOW or BEFORE since old event mixes notification and cancellation.
		// This internal event is between to allow cancellation without affecting world before BEFORE
		InternalEvents.BETWEEN_BLOCK_CANCEL_AND_BREAK.register((world, player, pos, state, blockEntity) -> {
			return PlayerBlockBreakEvents.BEFORE.invoker().beforeBlockBreak(world, player, pos, state, blockEntity);
		});

		ServerPlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
			PlayerBlockBreakEvents.AFTER.invoker().afterBlockBreak(world, player, pos, state, blockEntity);
		});

		ServerPlayerBlockBreakEvents.CANCELED.register((world, player, pos, state, blockEntity) -> {
			PlayerBlockBreakEvents.CANCELED.invoker().onBlockBreakCanceled(world, player, pos, state, blockEntity);
		});
	}
}
