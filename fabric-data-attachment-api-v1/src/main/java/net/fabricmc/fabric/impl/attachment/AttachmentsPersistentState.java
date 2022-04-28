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

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

public class AttachmentsPersistentState extends PersistentState {
	public static final String ID = "fabric_data_attachments";

	private final ServerWorld world;
	private final boolean wasSerialized;

	public AttachmentsPersistentState(ServerWorld world) {
		this.world = world;
		this.wasSerialized = ((AttachmentTargetImpl) world).hasSerializableAttachments();
	}

	public static AttachmentsPersistentState read(ServerWorld world, NbtCompound nbt) {
		((AttachmentTargetImpl) world).readAttachmentsFromNbt(World.class, nbt);
		return new AttachmentsPersistentState(world);
	}

	@Override
	public boolean isDirty() {
		// Only write data if there are attachments, or if we previously wrote data.
		return wasSerialized || ((AttachmentTargetImpl) world).hasSerializableAttachments();
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		((AttachmentTargetImpl) world).writeAttachmentsToNbt(nbt);
		return nbt;
	}
}
