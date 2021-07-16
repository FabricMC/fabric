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

package net.fabricmc.fabric.impl.transfer.context;

import java.util.List;
import java.util.Objects;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class PlayerContainerItemContext implements ContainerItemContext {
	private final PlayerInventoryStorage playerWrapper;
	private final SingleSlotStorage<ItemVariant> slot;
	private final World world;

	public PlayerContainerItemContext(PlayerEntity player, Hand hand) {
		Objects.requireNonNull(hand, "Hand may not be null.");

		this.playerWrapper = PlayerInventoryStorage.ofPlayer(player);
		int slotIndex = hand == Hand.MAIN_HAND ? player.getInventory().selectedSlot : PlayerInventory.OFF_HAND_SLOT;
		this.slot = playerWrapper.getSlots().get(slotIndex);
		this.world = player.getEntityWorld();
	}

	public PlayerContainerItemContext(PlayerEntity player, SingleSlotStorage<ItemVariant> slot) {
		this.playerWrapper = PlayerInventoryStorage.ofPlayer(player);
		this.slot = slot;
		this.world = player.getEntityWorld();
	}

	@Override
	public SingleSlotStorage<ItemVariant> getMainSlot() {
		return slot;
	}

	@Override
	public long insertOverflow(ItemVariant itemVariant, long maxAmount, TransactionContext transactionContext) {
		playerWrapper.offerOrDrop(itemVariant, maxAmount, transactionContext);
		return maxAmount;
	}

	@Override
	public List<SingleSlotStorage<ItemVariant>> getAdditionalSlots() {
		return playerWrapper.getSlots();
	}

	@Override
	public World getWorld() {
		return world;
	}
}
