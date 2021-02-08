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

package net.fabricmc.fabric.api.interaction.v1.event.player;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class ServerPlayerBlockInteractEvents {
	public static final Event<Allow> ALLOW = EventFactory.createArrayBacked(Allow.class, callbacks -> (player, world, hand, hit) -> {
		for (Allow callback : callbacks) {
			if (!callback.allowBlockUse(player, world, hand, hit)) {
				return false;
			}
		}

		return true;
	});

	public static final Event<InterceptDefaultAction> INTERCEPT_DEFAULT_ACTION = EventFactory.createArrayBacked(InterceptDefaultAction.class, callbacks -> (player, world, hand, hit) -> {
		for (InterceptDefaultAction callback : callbacks) {
			final ActionResult result = callback.interceptBlockUseAction(player, world, hand, hit);

			if (result != ActionResult.PASS) {
				return result;
			}
		}

		return ActionResult.PASS;
	});

	public static final Event<Before> BEFORE = EventFactory.createArrayBacked(Before.class, callbacks -> (player, world, hand, hit) -> {
		for (Before callback : callbacks) {
			callback.beforeBlockUse(player, world, hand, hit);
		}
	});

	// FIXME: Call after event
	public static final Event<After> AFTER = EventFactory.createArrayBacked(After.class, callbacks -> (player, world, hand, hit) -> {
		for (After callback : callbacks) {
			callback.afterBlockUse(player, world, hand, hit);
		}
	});

	public static final Event<Canceled> CANCELED = EventFactory.createArrayBacked(Canceled.class, callbacks -> (player, world, hand, hit) -> {
		for (Canceled callback : callbacks) {
			callback.onBlockUseCanceled(player, world, hand, hit);
		}
	});

	private ServerPlayerBlockInteractEvents() {
	}

	@FunctionalInterface
	public interface Allow {
		boolean allowBlockUse(ServerPlayerEntity player, ServerWorld world, Hand hand, BlockHitResult hit);
	}

	@FunctionalInterface
	public interface InterceptDefaultAction {
		ActionResult interceptBlockUseAction(ServerPlayerEntity player, ServerWorld world, Hand hand, BlockHitResult hit);
	}

	@FunctionalInterface
	public interface Before {
		void beforeBlockUse(ServerPlayerEntity player, ServerWorld world, Hand hand, BlockHitResult hit);
	}

	@FunctionalInterface
	public interface After {
		void afterBlockUse(ServerPlayerEntity player, ServerWorld world, Hand hand, BlockHitResult hit);
	}

	@FunctionalInterface
	public interface Canceled {
		void onBlockUseCanceled(ServerPlayerEntity player, ServerWorld world, Hand hand, BlockHitResult hit);
	}
}
