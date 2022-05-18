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

import java.util.Objects;

import net.minecraft.class_7492;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * A class for registering a {@link class_7492 ChatDecorator}. Check the chat decorator documentation
 * for how chat decorators work. Unlike other events, this uses a functional interface that is
 * provided by the vanilla game.
 *
 * <p>This event uses phases to provide better mod compatibilities between mods that add custom
 * content and styling. Chat decorators with the styling phase will always apply after the ones with
 * the content phase. When registering the chat decorator, it is recommended to choose one of
 * the phases from this interface and pass that to the {@link Event#register(Identifier, Object)}
 * function. If not given, the chat decorator will run in the default phase, which is between
 * the content phase and the styling phase.
 *
 * <p>Note that this API may <strong>cache the result of the chat decorator</strong>. If the player
 * sent a chat message that exactly matched the one the player had previewed just before, the
 * decorator will not be called again; instead, the cached, previously decorated message is used.
 * This is to avoid a pitfall when using externally controlled results as the decorated message,
 * because the server discards any messages whose decorated version differs from the previewed one.
 * If the player or the server disabled the chat preview, the cache will not be used.
 *
 * <p>Example of registering a content phase chat decorator:
 *
 * <pre><code>
 * ChatDecoratorEvent.EVENT.register(ChatDecoratorEvent.CONTENT_PHASE, (sender, message) -> {
 *     // Add smiley face. Has to copy() to get a MutableText.
 *     return message.copy().append(" :)");
 * });
 * </code></pre>
 *
 * <p>Example of registering a styling phase chat decorator:
 *
 * <pre><code>
 * ChatDecoratorEvent.EVENT.register(ChatDecoratorEvent.STYLING_PHASE, (sender, message) -> {
 *     // Apply orange color to messages sent by server operators
 *     if (sender != null && sender.server.getPlayerManager().isOperator(sender.getGameProfile())) {
 *         return message.copy().styled(style -> style.withColor(0xFFA500));
 *     }
 *     return message;
 * });
 * </code></pre>
 */
public final class ChatDecoratorEvent {
	/**
	 * The content phase of the event, passed when registering a chat decorator. Use this when
	 * the chat decorator modifies the text content of the message.
	 */
	public static Identifier CONTENT_PHASE = new Identifier("fabric-chat-api-v1", "content");
	/**
	 * The styling phase of the event, passed when registering a chat decorator. Use this when
	 * the chat decorator only modifies the styling of the message with the text intact.
	 */
	public static Identifier STYLING_PHASE = new Identifier("fabric-chat-api-v1", "styling");

	public static Event<class_7492> EVENT = EventFactory.createWithPhases(class_7492.class, decorators -> (sender, message) -> {
		for (class_7492 decorator : decorators) {
			message = Objects.requireNonNull(decorator.decorate(sender, message), "chat decorator must not return null");
		}

		return message;
	}, CONTENT_PHASE, Event.DEFAULT_PHASE, STYLING_PHASE);
}
