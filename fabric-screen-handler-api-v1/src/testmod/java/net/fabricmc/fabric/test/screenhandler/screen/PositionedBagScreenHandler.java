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

package net.fabricmc.fabric.test.screenhandler.screen;

import java.util.Optional;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.test.screenhandler.ScreenHandlerTest;

public class PositionedBagScreenHandler extends BagScreenHandler implements PositionedScreenHandler {
	private final BlockPos pos;

	public PositionedBagScreenHandler(int syncId, PlayerInventory playerInventory, BagData data) {
		this(syncId, playerInventory, new SimpleInventory(9), data.pos().orElse(null));
	}

	public PositionedBagScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, BlockPos pos) {
		super(ScreenHandlerTest.POSITIONED_BAG_SCREEN_HANDLER, syncId, playerInventory, inventory);
		this.pos = pos;
	}

	@Override
	public BlockPos getPos() {
		return pos;
	}

	public record BagData(Optional<BlockPos> pos) {
		public static final PacketCodec<RegistryByteBuf, BagData> PACKET_CODEC = BlockPos.PACKET_CODEC.collect(PacketCodecs::optional).xmap(BagData::new, BagData::pos).cast();
	}
}
