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

package net.fabricmc.fabric.api.chat.v1;

import net.minecraft.network.MessageType;
import net.minecraft.network.encryption.SignedChatMessage;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.Message;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.registry.RegistryKey;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class ServerChatEvents {
	private ServerChatEvents() {
	}

	/**
	 * Called when a player sends a chat message to all players, either via the chat hud or the
	 * {@code /say} command.
	 */
	public static final Event<SendChatMessage> SEND_CHAT_MESSAGE = EventFactory.createArrayBacked(SendChatMessage.class, handlers -> (message, sender, typeKey) -> {
		for (SendChatMessage handler : handlers) {
			handler.onSendChatMessage(message, sender, typeKey);
		}
	});

	/**
	 * Called when the server sends a game message to all players. Game message includes death
	 * messages, join/leave messages, and advancement messages.
	 */
	public static final Event<SendGameMessage> SEND_GAME_MESSAGE = EventFactory.createArrayBacked(SendGameMessage.class, handlers -> (message, typeKey) -> {
		for (SendGameMessage handler : handlers) {
			handler.onSendGameMessage(message, typeKey);
		}
	});

	/**
	 * Called when a command sends a message to all players. Note that if a player executed the
	 * command, {@link #SEND_CHAT_MESSAGE} will also be triggered. To check whether the player
	 * executed the command, use {@code source.getPlayer() != null}.
	 */
	public static final Event<SendCommandMessage> SEND_COMMAND_MESSAGE = EventFactory.createArrayBacked(SendCommandMessage.class, handlers -> (message, source, typeKey) -> {
		for (SendCommandMessage handler : handlers) {
			handler.onSendCommandMessage(message, source, typeKey);
		}
	});

	@FunctionalInterface
	public interface SendChatMessage {
		void onSendChatMessage(Message<SignedChatMessage> message, ServerPlayerEntity sender, RegistryKey<MessageType> typeKey);
	}

	@FunctionalInterface
	public interface SendGameMessage {
		void onSendGameMessage(Text message, RegistryKey<MessageType> typeKey);
	}

	@FunctionalInterface
	public interface SendCommandMessage {
		void onSendCommandMessage(Message<SignedChatMessage> message, ServerCommandSource source, RegistryKey<MessageType> typeKey);
	}
}
