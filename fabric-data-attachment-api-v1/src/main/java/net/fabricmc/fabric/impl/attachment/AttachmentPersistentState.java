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

package net.fabricmc.fabric.impl.attachment;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

/**
 * Backing storage for server-side world attachments.
 * Thanks to custom [.isDirty] logic, the file is only written if something needs to be persisted.
 * @author Technici4n
 */
public class AttachmentPersistentState extends PersistentState {
	public static final String ID = "fabric_data_attachments";
	private final AttachmentTargetImpl worldTarget;
	private final boolean wasSerialized;

	public AttachmentPersistentState(ServerWorld world) {
		this.worldTarget = (AttachmentTargetImpl) world;
		this.wasSerialized = worldTarget.fabric_hasPersistentAttachments();
	}

	public static AttachmentPersistentState read(ServerWorld world, @Nullable NbtCompound nbt) {
		((AttachmentTargetImpl) world).fabric_readAttachmentsFromNbt(nbt);
		return new AttachmentPersistentState(world);
	}

	@Override
	public boolean isDirty() {
		// Only write data if there are attachments, or if we previously wrote data.
		return wasSerialized || worldTarget.fabric_hasPersistentAttachments();
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		worldTarget.fabric_writeAttachmentsToNbt(nbt);
		return nbt;
	}
}
