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

package net.fabricmc.fabric.api.entity.event.v1;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.world.event.GameEvent;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * An event that allows listeners to enable elytra flight for players.
 */
public interface AllowElytraFlight {
	Event<AllowElytraFlight> EVENT = Util.make(EventFactory.createArrayBacked(AllowElytraFlight.class, listeners -> (player, damageElytra) -> {
		for (AllowElytraFlight listener : listeners) {
			if (listener.allowElytraFlight(player, damageElytra)) {
				return true;
			}
		}

		return false;
	}), event -> event.register((player, damageElytra) -> {
		ItemStack chestStack = player.getEquippedStack(EquipmentSlot.CHEST);

		if (chestStack.getItem() instanceof ChestItem chestItem) {
			return chestItem.allowElytraFlight(player, chestStack, damageElytra);
		}

		return false;
	}));

	/**
	 * Check if elytra flight is allowed.
	 *
	 * @param player The player.
	 * @param tickElytra False if this is just to check if elytra flight is allowed. True if the elytra should be ticked, i.e. damaged.
	 * @return True to allow elytra flight, and cancel subsequent handlers. False to disallow elytra flight, unless a subsequent listener allows it.
	 */
	boolean allowElytraFlight(PlayerEntity player, boolean tickElytra);

	/**
	 * An interface that can be implemented on an item to enable elytra flight when it is worn in the {@link EquipmentSlot#CHEST} slot.
	 */
	interface ChestItem {
		/**
		 * Check if elytra flight is allowed.
		 *
		 * @param player The player.
		 * @param chestStack The stack currently worn in the chest slot. Will always be of this item.
		 * @param tickElytra True to tick the elytra, false to only perform the check. Vanilla-like elytras can use {@link #doVanillaElytraTick} to handle ticking.
		 * @return true to allow elytra flight.
		 */
		default boolean allowElytraFlight(PlayerEntity player, ItemStack chestStack, boolean tickElytra) {
			if (ElytraItem.isUsable(chestStack)) {
				if (tickElytra) doVanillaElytraTick(player, chestStack);
				return true;
			}

			return false;
		}

		/**
		 * A helper to perform the default vanilla elytra tick logic: damage the elytra every 20 ticks, and send a game event every 10 ticks.
		 */
		default void doVanillaElytraTick(PlayerEntity player, ItemStack chestStack) {
			int nextRoll = player.getRoll() + 1;

			if (!player.world.isClient && nextRoll % 10 == 0) {
				if ((nextRoll / 10) % 2 == 0) {
					chestStack.damage(1, player, p -> p.sendEquipmentBreakStatus(EquipmentSlot.CHEST));
				}

				player.emitGameEvent(GameEvent.ELYTRA_FREE_FALL);
			}
		}
	}
}
