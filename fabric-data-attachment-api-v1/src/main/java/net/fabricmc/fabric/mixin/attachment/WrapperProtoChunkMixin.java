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

package net.fabricmc.fabric.mixin.attachment;

import java.util.Map;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.WrapperProtoChunk;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.impl.attachment.AttachmentTargetImpl;

@Mixin(WrapperProtoChunk.class)
public class WrapperProtoChunkMixin implements AttachmentTargetImpl {
	@Shadow
	@Final
	private WorldChunk wrapped;

	@Override
	@Nullable
	public <T> T getAttached(AttachmentType<T> type) {
		return this.wrapped.getAttached(type);
	}

	@Override
	@Nullable
	public <T> T setAttached(AttachmentType<T> type, @Nullable T value) {
		return this.wrapped.setAttached(type, value);
	}

	@Override
	public boolean hasAttached(AttachmentType<?> type) {
		return this.wrapped.hasAttached(type);
	}

	@Override
	public void fabric_writeAttachmentsToNbt(NbtCompound nbt) {
		((AttachmentTargetImpl) this.wrapped).fabric_writeAttachmentsToNbt(nbt);
	}

	@Override
	public void fabric_readAttachmentsFromNbt(NbtCompound nbt) {
		((AttachmentTargetImpl) this.wrapped).fabric_readAttachmentsFromNbt(nbt);
	}

	@Override
	public boolean fabric_hasPersistentAttachments() {
		return ((AttachmentTargetImpl) this.wrapped).fabric_hasPersistentAttachments();
	}

	@Override
	public Map<AttachmentType<?>, ?> fabric_getAttachments() {
		return ((AttachmentTargetImpl) this.wrapped).fabric_getAttachments();
	}
}
