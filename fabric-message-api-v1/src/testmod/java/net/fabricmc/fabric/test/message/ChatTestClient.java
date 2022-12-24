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

package net.fabricmc.fabric.test.message;

import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;

public class ChatTestClient implements ClientModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChatTestClient.class);

	@Override
	public void onInitializeClient() {
		//Test client send message events
		ClientSendMessageEvents.ALLOW_CHAT.register((message) -> {
			if (message.contains("blocked")) {
				LOGGER.info("Blocked chat message: " + message);
				return false;
			}

			return true;
		});
		ClientSendMessageEvents.CHAT.register((message) -> LOGGER.info("Sent chat message: " + message));
		ClientSendMessageEvents.CHAT_CANCELED.register((message) -> LOGGER.info("Canceled sending chat message: " + message));
		//Test client send command events
		ClientSendMessageEvents.ALLOW_COMMAND.register((command) -> {
			if (command.contains("blocked")) {
				LOGGER.info("Blocked chat message: " + command);
				return false;
			}

			return true;
		});
		ClientSendMessageEvents.COMMAND.register((command) -> LOGGER.info("Sent command message: " + command));
		ClientSendMessageEvents.COMMAND_CANCELED.register((command) -> LOGGER.info("Canceled sending command message: " + command));
		//Test client receive message events
		ClientReceiveMessageEvents.ALLOW_CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> {
			if (message.getString().contains("block receive")) {
				LOGGER.info("Blocked receiving chat message: " + message.getString());
				return false;
			}

			return true;
		});
		ClientReceiveMessageEvents.CHAT.register((message, signedMessage, sender, params, receptionTimestamp) -> LOGGER.info("Received chat message sent by {} at time {}: {}", sender == null ? "null" : sender.getName(), receptionTimestamp.toEpochMilli(), message.getString()));
		ClientReceiveMessageEvents.CHAT_CANCELED.register((message, signedMessage, sender, params, receptionTimestamp) -> LOGGER.info("Cancelled receiving chat message sent by {} at time {}: {}", sender == null ? "null" : sender.getName(), receptionTimestamp.toEpochMilli(), message.getString()));
		//Test client receive game message events
		ClientReceiveMessageEvents.ALLOW_GAME.register((message, overlay) -> {
			if (message.getString().startsWith("Unknown or incomplete command")) {
				LOGGER.info("Blocked receiving \"unknown or incomplete command\" message: " + message.getString());
				return false;
			}

			return true;
		});
		ClientReceiveMessageEvents.GAME.register((message, overlay) -> LOGGER.info("Received game message with overlay {}: {}", overlay, message.getString()));
		ClientReceiveMessageEvents.GAME_CANCELED.register((message, overlay) -> LOGGER.info("Cancelled receiving game message with overlay {}: {}", overlay, message.getString()));
	}
}
