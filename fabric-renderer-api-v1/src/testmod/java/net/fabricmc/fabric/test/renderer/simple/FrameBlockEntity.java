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

package net.fabricmc.fabric.test.renderer.simple;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;

import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.fabricmc.fabric.api.util.NbtType;
import net.fabricmc.fabric.test.renderer.WorldRenderExtensions;

public final class FrameBlockEntity extends BlockEntity implements RenderAttachmentBlockEntity {
	@Nullable
	private Block block = null;

	public FrameBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(RendererTest.FRAME_BLOCK_ENTITY, blockPos, blockState);
	}

	@Override
	public void readNbt(NbtCompound tag) {
		super.readNbt(tag);

		if (tag.contains("block", NbtType.STRING)) {
			this.block = Registry.BLOCK.get(new Identifier(tag.getString("block")));
		} else {
			this.block = null;
		}

		if (this.getWorld() != null && this.getWorld().isClient()) {
			WorldRenderExtensions.scheduleBlockRerender(this.getWorld(), this.getPos());
		}
	}

	@Override
	public void writeNbt(NbtCompound tag) {
		if (this.block != null) {
			tag.putString("block", Registry.BLOCK.getId(this.block).toString());
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
	public Block getRenderAttachmentData() {
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
