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

package net.fabricmc.fabric.api.attachment.v1;

import java.util.function.BiPredicate;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.server.network.ServerPlayerEntity;

import net.fabricmc.fabric.impl.attachment.sync.AttachmentSyncImpl;

/**
 * A class with specific methods related to attachment syncing.
 */
public final class AttachmentSync {
	private AttachmentSync() {
	}

	/**
	 * Refreshes all attachments that have been synchronized with the given {@link ServerPlayerEntity}'s client.
	 *
	 * <p>For attachment types registered with {@link AttachmentRegistry.Builder#syncWithCustom(PacketCodec, BiPredicate) syncWithCustom},
	 * the set of players with which the attachments should be synchronized with can change during runtime. For example,
	 * an attachment that should only be synchronized with operators, when a player changes operator status.</p>
	 *
	 * <p>Calling this method makes ensures the client only has the attachment data it is supposed to be aware of.</p>
	 *
	 * @param player the player whose client's attachments will be refreshed
	 */
	public static void refreshAttachments(ServerPlayerEntity player) {
		AttachmentSyncImpl.refreshAttachments(player);
	}
}
