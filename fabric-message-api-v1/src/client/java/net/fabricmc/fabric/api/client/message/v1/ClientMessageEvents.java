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

package net.fabricmc.fabric.api.client.message.v1;

import net.minecraft.network.message.MessageSender;
import net.minecraft.network.message.MessageType;
import net.minecraft.text.Text;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@Environment(EnvType.CLIENT)
public final class ClientMessageEvents {
	/**
	 * An event triggered when the client receives a chat message sent by the player or
	 * commands such as {@code /me}, {@code /msg}, {@code /say}, or {@code /teammsg}, including
	 * ones executed using command block or sent from non-player entities.
	 *
	 * <p>This event will not be called if the message is blocked for some reason,
	 * such as the message signature verification failing with "Only Show Secure Chat" option
	 * enabled, or the sender being blocked via the social interactions screen.
	 */
	public static final Event<ChatMessage> CHAT_MESSAGE = EventFactory.createArrayBacked(ChatMessage.class, (handlers) -> (message, sender, type) -> {
		for (ChatMessage handler : handlers) {
			handler.onChatMessage(message, sender, type);
		}
	});

	/**
	 * An event triggered when the client receives a game message from the server. Game
	 * messages include death messages, join/leave messages, and advancement messages.
	 */
	public static final Event<GameMessage> GAME_MESSAGE = EventFactory.createArrayBacked(GameMessage.class, (handlers) -> (message, type) -> {
		for (GameMessage handler : handlers) {
			handler.onGameMessage(message, type);
		}
	});

	public interface ChatMessage {
		/**
		 * Called when the client receives a chat message sent by the player or
		 * commands such as {@code /me}, {@code /msg}, {@code /say}, or {@code /teammsg}, including
		 * ones executed using command block or as entities using {@code /execute}. To
		 * distinguish the source of the message, compare the passed message type.
		 *
		 * <p>This will not be called if the message is blocked for some reason,
		 * such as the message signature verification failing with "Only Show Secure Chat" option
		 * enabled, or the sender being blocked via the social interactions screen.
		 * @param message the received message
		 * @param sender the sender; use methods in {@link ClientMessageHelper} to convert this
		 * @param type the message type
		 * @see ClientMessageHelper
		 */
		void onChatMessage(Text message, MessageSender sender, MessageType type);
	}

	public interface GameMessage {
		/**
		 * Called when the client receives a game message from the server. Game
		 * messages include death messages, join/leave messages, and advancement messages.
		 * @param message the received message
		 * @param type the message type
		 */
		void onGameMessage(Text message, MessageType type);
	}
}
