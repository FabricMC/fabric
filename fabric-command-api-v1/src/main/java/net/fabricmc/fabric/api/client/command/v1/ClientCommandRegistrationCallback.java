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

package net.fabricmc.fabric.api.client.command.v1;

import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.impl.command.client.ClientCommandInternals;

/**
 * Callback for registering client-sided commands.
 *
 * <p>Client-sided commands are fully executed on the client,
 * so players can use them in both singleplayer and multiplayer.
 *
 * <p>Client-sided commands also support using custom command
 * prefixes instead of the {@code /} symbol. You can customize
 * the used prefix with {@link #event(char)}.
 *
 * <p>The commands are run on the client game thread by default.
 * Avoid doing any heavy calculations here as that can freeze the game's rendering.
 * For example, you can move heavy code to another thread.
 *
 * <h2>Example</h2>
 * <pre>
 * {@code
 * ClientCommandRegistrationCallback.event().register(dispatcher -> {
 *     dispatcher.register(ArgumentBuilders.literal("hello").executes(context -> {
 *         context.getSource().sendFeedback(new LiteralText("Hello, world!"));
 *         return 0;
 *     }));
 * });
 * }
 * </pre>
 */
@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface ClientCommandRegistrationCallback {
	/**
	 * Gets the command registration event for commands with the {@code /} prefix.
	 *
	 * @return the event object
	 */
	static Event<ClientCommandRegistrationCallback> event() {
		return event('/');
	}

	/**
	 * Gets the command registration event with a custom command prefix.
	 *
	 * @param prefix the command prefix
	 * @return the event object
	 * @throws IllegalArgumentException if the prefix {@linkplain Character#isLetterOrDigit(char) is a letter or a digit},
	 *                                  or if it {@linkplain Character#isWhitespace(char) is whitespace}.
	 */
	static Event<ClientCommandRegistrationCallback> event(char prefix) {
		return ClientCommandInternals.event(prefix);
	}

	/**
	 * Called when a client-side command dispatcher is registering commands.
	 *
	 * @param dispatcher the command dispatcher to register commands to
	 */
	void register(CommandDispatcher<FabricClientCommandSource> dispatcher);
}
