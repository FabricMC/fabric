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

package net.fabricmc.fabric.api.message.v1;

import net.minecraft.network.MessageType;
import net.minecraft.network.encryption.SignedChatMessage;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.registry.RegistryKey;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class ServerChatEvents {
	private ServerChatEvents() {
	}

	/**
	 * An event that is called when a player sends a chat message to all players, either via the
	 * chat hud or the {@code /say} command. Mods can use this event to block the message.
	 */
	public static final Event<AllowChatMessage> ALLOW_CHAT_MESSAGE = EventFactory.createArrayBacked(AllowChatMessage.class, handlers -> (message, sender, typeKey) -> {
		for (AllowChatMessage handler : handlers) {
			if (!handler.allowChatMessage(message, sender, typeKey)) return false;
		}

		return true;
	});

	/**
	 * An event that is called when the server sends a game message to all players. Game messages
	 * include death messages, join/leave messages, and advancement messages. Mods can use this
	 * event to block the message.
	 */
	public static final Event<AllowGameMessage> ALLOW_GAME_MESSAGE = EventFactory.createArrayBacked(AllowGameMessage.class, handlers -> (message, typeKey) -> {
		for (AllowGameMessage handler : handlers) {
			if (!handler.allowGameMessage(message, typeKey)) return false;
		}

		return true;
	});

	/**
	 * An event that is called when a command sends a message to all players. Note that if a player
	 * executed the command, {@link #ALLOW_CHAT_MESSAGE} will also be triggered. To check whether the
	 * player executed the command, use {@code source.getPlayer() != null}. Mods can use this event
	 * to block the message.
	 */
	public static final Event<AllowCommandMessage> ALLOW_COMMAND_MESSAGE = EventFactory.createArrayBacked(AllowCommandMessage.class, handlers -> (message, source, typeKey) -> {
		for (AllowCommandMessage handler : handlers) {
			if (!handler.allowCommandMessage(message, source, typeKey)) return false;
		}

		return true;
	});

	/**
	 * An event that is called when a player sends a chat message to all players, either via the
	 * chat hud or the {@code /say} command.
	 */
	public static final Event<SendChatMessage> SEND_CHAT_MESSAGE = EventFactory.createArrayBacked(SendChatMessage.class, handlers -> (message, sender, typeKey) -> {
		for (SendChatMessage handler : handlers) {
			handler.onSendChatMessage(message, sender, typeKey);
		}
	});

	/**
	 * An event that is called when the server sends a game message to all players. Game messages
	 * include death messages, join/leave messages, and advancement messages.
	 */
	public static final Event<SendGameMessage> SEND_GAME_MESSAGE = EventFactory.createArrayBacked(SendGameMessage.class, handlers -> (message, typeKey) -> {
		for (SendGameMessage handler : handlers) {
			handler.onSendGameMessage(message, typeKey);
		}
	});

	/**
	 * An event that is called when a command sends a message to all players. Note that if a player
	 * executed the command, {@link #SEND_CHAT_MESSAGE} will also be triggered. To check whether the
	 * player executed the command, use {@code source.getPlayer() != null}.
	 */
	public static final Event<SendCommandMessage> SEND_COMMAND_MESSAGE = EventFactory.createArrayBacked(SendCommandMessage.class, handlers -> (message, source, typeKey) -> {
		for (SendCommandMessage handler : handlers) {
			handler.onSendCommandMessage(message, source, typeKey);
		}
	});

	@FunctionalInterface
	public interface AllowChatMessage {
		/**
		 * Called when a player sends a chat message to all players, either via the
		 * chat hud or the {@code /say} command.
		 * @param message the sent message; use {@code message.raw().getContent()} to get the text
		 * @param sender the player that sent the message
		 * @param typeKey the message type
		 * @return {@code true} if the message should be sent, {@code false} otherwise
		 */
		boolean allowChatMessage(FilteredMessage<SignedChatMessage> message, ServerPlayerEntity sender, RegistryKey<MessageType> typeKey);
	}

	@FunctionalInterface
	public interface AllowGameMessage {
		/**
		 * Called when the server sends a game message to all players. Game messages
		 * include death messages, join/leave messages, and advancement messages.
		 * @param message the sent message; use {@code message.raw().getContent()} to get the text
		 * @param typeKey the message type
		 * @return {@code true} if the message should be sent, {@code false} otherwise
		 */
		boolean allowGameMessage(Text message, RegistryKey<MessageType> typeKey);
	}

	@FunctionalInterface
	public interface AllowCommandMessage {
		/**
		 * Called when a command sends a chat message to all players.
		 * @param message the sent message; use {@code message.raw().getContent()} to get the text
		 * @param source the command source that sent the message
		 * @param typeKey the message type
		 * @return {@code true} if the message should be sent, {@code false} otherwise
		 */
		boolean allowCommandMessage(FilteredMessage<SignedChatMessage> message, ServerCommandSource source, RegistryKey<MessageType> typeKey);
	}

	@FunctionalInterface
	public interface SendChatMessage {
		/**
		 * Called when a player sends a chat message to all players, either via the
		 * chat hud or the {@code /say} command.
		 * @param message the sent message; use {@code message.raw().getContent()} to get the text
		 * @param sender the player that sent the message
		 * @param typeKey the message type
		 */
		void onSendChatMessage(FilteredMessage<SignedChatMessage> message, ServerPlayerEntity sender, RegistryKey<MessageType> typeKey);
	}

	@FunctionalInterface
	public interface SendGameMessage {
		/**
		 * Called when the server sends a game message to all players. Game messages
		 * include death messages, join/leave messages, and advancement messages.
		 * @param message the sent message; use {@code message.raw().getContent()} to get the text
		 * @param typeKey the message type
		 */
		void onSendGameMessage(Text message, RegistryKey<MessageType> typeKey);
	}

	@FunctionalInterface
	public interface SendCommandMessage {
		/**
		 * Called when a command sends a chat message to all players.
		 * @param message the sent message; use {@code message.raw().getContent()} to get the text
		 * @param source the command source that sent the message
		 * @param typeKey the message type
		 */
		void onSendCommandMessage(FilteredMessage<SignedChatMessage> message, ServerCommandSource source, RegistryKey<MessageType> typeKey);
	}
}
