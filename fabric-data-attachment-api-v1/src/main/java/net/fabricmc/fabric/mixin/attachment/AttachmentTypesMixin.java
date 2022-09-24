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

import java.util.IdentityHashMap;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.impl.attachment.AttachmentTargetImpl;

@Mixin(value = {BlockEntity.class, Entity.class, World.class, WorldChunk.class })
public class AttachmentTypesMixin implements AttachmentTargetImpl {
	@Nullable
	protected IdentityHashMap<AttachmentType<?, ?>, Object> fabric_dataAttachments = null;

	@Override
	public <T> T get(AttachmentType<T, ?> type) {
		return fabric_dataAttachments == null ? null : (T) fabric_dataAttachments.get(type);
	}

	@Override
	@Nullable
	public <T> T set(AttachmentType<T, ?> type, T value) {
		if (value == null) {
			if (fabric_dataAttachments == null) {
				return null;
			}

			T returned = (T) fabric_dataAttachments.remove(type);

			if (fabric_dataAttachments.isEmpty()) {
				fabric_dataAttachments = null;
			}

			return returned;
		} else {
			if (fabric_dataAttachments == null) {
				fabric_dataAttachments = new IdentityHashMap<>();
			}

			return (T) fabric_dataAttachments.put(type, value);
		}
	}

	@Override
	@Nullable
	public IdentityHashMap<AttachmentType<?, ?>, Object> getAttachmentsHolder() {
		return fabric_dataAttachments;
	}

	@Override
	public void setAttachmentsHolder(@Nullable IdentityHashMap<AttachmentType<?, ?>, Object> map) {
		fabric_dataAttachments = map;
	}
}
