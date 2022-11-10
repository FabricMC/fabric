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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.message.v1.ClientMessageEvents;

public class ChatTestClient implements ClientModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChatTestClient.class);

	@Override
	public void onInitializeClient() {
		//Test client send message events
		ClientMessageEvents.ALLOW_SEND_CHAT_MESSAGE.register((message) -> {
			if (message.contains("blocked")) {
				LOGGER.info("Blocked chat message: " + message);
				return false;
			}

			return true;
		});
		ClientMessageEvents.SEND_CHAT_MESSAGE.register((message) -> LOGGER.info("Sent chat message: " + message));
		//Test client send command events
		ClientMessageEvents.ALLOW_SEND_COMMAND_MESSAGE.register((command) -> {
			if (command.contains("blocked")) {
				LOGGER.info("Blocked chat message: " + command);
				return false;
			}

			return true;
		});
		ClientMessageEvents.SEND_COMMAND_MESSAGE.register((command) -> LOGGER.info("Sent command message: " + command));
		//Test client receive message events
		ClientMessageEvents.ALLOW_RECEIVE_CHAT_MESSAGE.register((message, signedMessage, sender, params, receptionTimestamp) -> {
			if (message.getString().contains("block receive")) {
				LOGGER.info("Blocked receiving chat message: " + message.getString());
				return false;
			}

			return true;
		});
		ClientMessageEvents.RECEIVE_CHAT_MESSAGE.register((message, signedMessage, sender, params, receptionTimestamp) -> LOGGER.info("Received chat message sent by {} at time {}: {}", sender == null ? "null" : sender.getName(), receptionTimestamp.toEpochMilli(), message.getString()));
		//Test client receive game message events
		ClientMessageEvents.ALLOW_RECEIVE_GAME_MESSAGE.register((message, overlay) -> {
			if (message.getString().startsWith("Unknown or incomplete command")) {
				LOGGER.info("Blocked receiving \"unknown or incomplete command\" message: " + message.getString());
				return false;
			}

			return true;
		});
		ClientMessageEvents.RECEIVE_GAME_MESSAGE.register((message, overlay) -> LOGGER.info("Received game message with overlay {}: {}", overlay, message.getString()));
	}
}
