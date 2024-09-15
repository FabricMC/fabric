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

import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

/*
 * Interface to allow block entities to communicate with their parent chunk. Initial sync data
 * (as in AttachmentTargetImpl#fabric_getInitialSyncChanges) of block entities is sent at the same time as that of
 * their chunk for convenience, because the game sends chunk and block entity data at the same time anyway.
 * Thus, all the initial sync data for block entities is tracked on the parent WorldChunk, using this interface.
 */
public interface BlockEntityAttachmentReceiver {
	default void fabric_acknowledgeBlockEntityAttachment(BlockPos pos, AttachmentType<?> type, @Nullable Object value) {
		// shouldn't do anything outside of WorldChunk, but needs testing
	}
}
