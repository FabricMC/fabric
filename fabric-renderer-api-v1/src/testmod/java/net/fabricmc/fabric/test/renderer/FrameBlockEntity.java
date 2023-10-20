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

package net.fabricmc.fabric.test.renderer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.api.blockview.v2.RenderDataBlockEntity;

public class FrameBlockEntity extends BlockEntity implements RenderDataBlockEntity {
	@Nullable
	private Block block = null;

	public FrameBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(Registration.FRAME_BLOCK_ENTITY_TYPE, blockPos, blockState);
	}

	@Override
	public void readNbt(NbtCompound tag) {
		super.readNbt(tag);

		if (tag.contains("block", NbtElement.STRING_TYPE)) {
			this.block = Registries.BLOCK.get(new Identifier(tag.getString("block")));
		} else {
			this.block = null;
		}

		if (this.getWorld() != null && this.getWorld().isClient()) {
			// This call forces a chunk remesh.
			world.updateListeners(pos, null, null, 0);
		}
	}

	@Override
	public void writeNbt(NbtCompound tag) {
		if (this.block != null) {
			tag.putString("block", Registries.BLOCK.getId(this.block).toString());
		} else {
			// Always need something in the tag, otherwise S2C syncing will never apply the packet.
			tag.putInt("block", -1);
		}
	}

	@Override
	public void markDirty() {
		super.markDirty();

		if (this.hasWorld() && !this.getWorld().isClient()) {
			((ServerWorld) world).getChunkManager().markForUpdate(getPos());
		}
	}

	@Nullable
	public Block getBlock() {
		return this.block;
	}

	public void setBlock(@Nullable Block block) {
		this.block = block;
		this.markDirty();
	}

	@Nullable
	@Override
	public Block getRenderData() {
		return this.block;
	}

	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		return this.createNbt();
	}
}
