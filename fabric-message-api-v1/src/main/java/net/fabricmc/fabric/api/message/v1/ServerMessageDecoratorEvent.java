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

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.message.MessageDecorator;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * A class for registering a {@link MessageDecorator}. Check the message decorator documentation
 * for how message decorators work. Unlike other events, this uses a functional interface that is
 * provided by the vanilla game.
 *
 * <p>This event uses phases to provide better mod compatibilities between mods that add custom
 * content and styling. Message decorators with the styling phase will always apply after the ones
 * with the content phase. When registering the message decorator, it is recommended to choose one
 * of the phases from this interface and pass that to the {@link Event#register(Identifier, Object)}
 * function. If not given, the message decorator will run in the default phase, which is between
 * the content phase and the styling phase.
 *
 * <p>The message decorator's result is cached (as of 1.19.1) if the chat preview is enabled.
 * If the original message did not change between the last preview and submission, the decorator
 * is not called during submission and the cached preview is used instead. Note that the
 * decorator can still be called during submission if the chat preview is disabled, the
 * sent message was not the same as the previewed message, or if text filtering was enabled and
 * it produced a different message.
 *
 * <p>Example of registering a content phase message decorator:
 *
 * <pre>{@code
 * ServerMessageDecoratorEvent.EVENT.register(ServerMessageDecoratorEvent.CONTENT_PHASE, (sender, message) -> {
 *     // Add smiley face. Has to copy() to get a MutableText with siblings and styles.
 *     return message.copy().append(" :)");
 * });
 * }</pre>
 *
 * <p>Example of registering a styling phase message decorator:
 *
 * <pre>{@code
 * ServerMessageDecoratorEvent.EVENT.register(ServerMessageDecoratorEvent.STYLING_PHASE, (sender, message) -> {
 *     // Apply orange color to messages sent by server operators
 *     if (sender != null && sender.server.getPlayerManager().isOperator(sender.getGameProfile())) {
 *         return CompletableFuture.completedFuture(
 *             message.copy().styled(style -> style.withColor(0xFFA500)));
 *     }
 *     return CompletableFuture.completedFuture(message);
 * });
 * }</pre>
 */
public final class ServerMessageDecoratorEvent {
	private ServerMessageDecoratorEvent() {
	}

	/**
	 * The content phase of the event, passed when registering a message decorator. Use this when
	 * the decorator modifies the text content of the message.
	 */
	public static final Identifier CONTENT_PHASE = new Identifier("fabric", "content");
	/**
	 * The styling phase of the event, passed when registering a message decorator. Use this when
	 * the decorator only modifies the styling of the message with the text intact.
	 */
	public static final Identifier STYLING_PHASE = new Identifier("fabric", "styling");

	public static final Event<MessageDecorator> EVENT = EventFactory.createWithPhases(MessageDecorator.class, decorators -> (sender, message) -> {
		CompletableFuture<Text> future = null;

		for (MessageDecorator decorator : decorators) {
			if (future == null) {
				future = decorator.decorate(sender, message).handle((decorated, throwable) -> handle(decorated, throwable, decorator));
			} else {
				future = future.thenCompose((decorated) -> decorator.decorate(sender, decorated).handle((newlyDecorated, throwable) -> handle(newlyDecorated, throwable, decorator)));
			}
		}

		return future == null ? CompletableFuture.completedFuture(message) : future;
	}, CONTENT_PHASE, Event.DEFAULT_PHASE, STYLING_PHASE);

	private static <T extends Text> T handle(T decorated, @Nullable Throwable throwable, MessageDecorator decorator) {
		String decoratorName = decorator.getClass().getName();

		if (throwable != null) {
			if (throwable instanceof CompletionException) throwable = throwable.getCause();
			throw new CompletionException("message decorator %s failed".formatted(decoratorName), throwable);
		}

		return Objects.requireNonNull(decorated, "message decorator %s returned null".formatted(decoratorName));
	}
}
