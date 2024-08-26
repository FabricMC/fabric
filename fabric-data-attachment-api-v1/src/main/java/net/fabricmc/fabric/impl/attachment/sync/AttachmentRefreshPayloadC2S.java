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

package net.fabricmc.fabric.impl.attachment.sync;

import java.util.List;
import java.util.Map;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.impl.attachment.AttachmentRegistryImpl;

/*
 * After the initial packet, client responds with the attachments that may need to be refreshed (i.e those with
 * CUSTOM SyncType).
 */
public record AttachmentRefreshPayloadC2S(List<Map.Entry<AttachmentType<?>, AttachmentTargetInfo<?>>> attachments) implements CustomPayload {
	private static final PacketCodec<PacketByteBuf, Map.Entry<AttachmentType<?>, AttachmentTargetInfo<?>>> ENTRY_CODEC = PacketCodec.tuple(
			Identifier.PACKET_CODEC.xmap(AttachmentRegistryImpl::get, AttachmentType::identifier), Map.Entry::getKey,
			AttachmentTargetInfo.PACKET_CODEC, Map.Entry::getValue,
			Map::entry
	);
	public static final PacketCodec<PacketByteBuf, AttachmentRefreshPayloadC2S> CODEC = PacketCodec.tuple(
			ENTRY_CODEC.collect(PacketCodecs.toList()), AttachmentRefreshPayloadC2S::attachments,
			AttachmentRefreshPayloadC2S::new
	);
	public static final Id<AttachmentRefreshPayloadC2S> ID = new Id<>(AttachmentSyncImpl.REFRESH_PACKET_ID);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
