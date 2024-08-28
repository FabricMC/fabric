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

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentSync;
import net.fabricmc.fabric.impl.attachment.sync.s2c.AcceptedAttachmentsPayloadS2C;
import net.fabricmc.fabric.impl.attachment.sync.s2c.AttachmentSyncPayload;

public class AttachmentSyncClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// config
		ClientConfigurationNetworking.registerGlobalReceiver(AcceptedAttachmentsPayloadS2C.ID, (payload, context) -> {
			context.responseSender().sendPacket(AttachmentSync.createResponsePayload());
		});

		// play
		ClientPlayNetworking.registerGlobalReceiver(AttachmentSyncPayload.ID, (payload, context) ->
				context.client().submit(
						() -> payload.attachments()
								.forEach(attachmentChange -> attachmentChange.apply(context.client().world))
				)
		);
	}
}
