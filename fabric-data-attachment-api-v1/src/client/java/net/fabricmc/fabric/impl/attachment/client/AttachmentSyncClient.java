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

package net.fabricmc.fabric.impl.attachment.client;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.client.world.ClientWorld;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.impl.attachment.sync.AcceptedAttachmentsPayloadS2C;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentChange;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentRefreshPayloadC2S;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentRefreshPayloadS2C;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentSyncImpl;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentSyncPayload;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentTargetInfo;

public class AttachmentSyncClient implements ClientModInitializer {
	private Multimap<AttachmentType<?>, AttachmentTargetInfo<?>> localAttachments = HashMultimap.create();

	@Override
	public void onInitializeClient() {
		// config
		ClientConfigurationNetworking.registerGlobalReceiver(AcceptedAttachmentsPayloadS2C.ID, (payload, context) -> {
			context.responseSender().sendPacket(AttachmentSyncImpl.createResponsePayload());
		});

		// play
		ClientPlayNetworking.registerGlobalReceiver(AttachmentSyncPayload.ID, (payload, context) ->
				context.client().submit(
						() -> payload.attachments().forEach(attachmentChange -> {
							attachmentChange.apply(context.client().world);

							if (attachmentChange.data() != null) {
								localAttachments.put(attachmentChange.type(), attachmentChange.targetInfo());
							} else {
								localAttachments.remove(attachmentChange.type(), attachmentChange.targetInfo());
							}
						})
				)
		);

		ClientPlayNetworking.registerGlobalReceiver(AttachmentRefreshPayloadS2C.ID, (payload, context) -> {
			payload.attachments().ifPresentOrElse(
					attachments -> {
						// server response, apply changes
						context.client().submit(
								() -> {
									ClientWorld world = context.client().world;
									localAttachments.forEach(
											(type, targetInfo) -> targetInfo.getTarget(world).removeAttached(type)
									);

									for (AttachmentChange attachment : attachments) {
										attachment.apply(world);
									}
								}
						);
					},
					() -> {
						// first server packet, reply with local attachment data
						context.responseSender().sendPacket(
								new AttachmentRefreshPayloadC2S(localAttachments.entries().stream().toList())
						);
					}
			);
		});
	}
}
