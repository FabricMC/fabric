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

package net.fabricmc.fabric.impl.attachment.sync.s2c;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import net.fabricmc.fabric.impl.attachment.sync.AttachmentSync;

public class AcceptedAttachmentsPayloadS2C implements CustomPayload {
	public static final AcceptedAttachmentsPayloadS2C INSTANCE = new AcceptedAttachmentsPayloadS2C();
	public static final Id<AcceptedAttachmentsPayloadS2C> ID = new Id<>(AttachmentSync.CONFIG_PACKET_ID);
	public static final PacketCodec<PacketByteBuf, AcceptedAttachmentsPayloadS2C> CODEC = PacketCodec.unit(INSTANCE);

	private AcceptedAttachmentsPayloadS2C() {
	}

	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}
