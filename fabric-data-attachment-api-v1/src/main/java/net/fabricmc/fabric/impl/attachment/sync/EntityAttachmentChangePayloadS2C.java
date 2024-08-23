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

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record EntityAttachmentChangePayloadS2C(int entityId, List<AttachmentChange> changes) implements CustomPayload {
	public static final PacketCodec<PacketByteBuf, EntityAttachmentChangePayloadS2C> CODEC = PacketCodec.tuple(
			PacketCodecs.VAR_INT, EntityAttachmentChangePayloadS2C::entityId,
			AttachmentChange.LIST_PACKET_CODEC, EntityAttachmentChangePayloadS2C::changes,
			EntityAttachmentChangePayloadS2C::new
	);
	public static final Id<EntityAttachmentChangePayloadS2C> ID = new Id<>(AttachmentSync.ENTITY_PACKET_ID);

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
