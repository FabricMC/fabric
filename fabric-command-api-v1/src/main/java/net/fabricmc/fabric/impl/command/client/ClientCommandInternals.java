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

package net.fabricmc.fabric.impl.command.client;

import java.util.HashMap;
import java.util.Map;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandException;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@Environment(EnvType.CLIENT)
public final class ClientCommandInternals {
	private static final Logger LOGGER = LogManager.getLogger();

	private static final Map<Character, Event<ClientCommandRegistrationCallback>> events = new HashMap<>();
	private static final Map<Character, CommandDispatcher<FabricClientCommandSource>> dispatchers = new HashMap<>();

	public static Event<ClientCommandRegistrationCallback> event(char prefix) {
		if (isInvalidCommandPrefix(prefix)) {
			throw new IllegalArgumentException("Command prefix '" + prefix + "' cannot be a letter, a digit or whitespace!");
		}

		return events.computeIfAbsent(prefix, c -> EventFactory.createArrayBacked(ClientCommandRegistrationCallback.class, callbacks -> dispatcher -> {
			for (ClientCommandRegistrationCallback callback : callbacks) {
				callback.register(dispatcher);
			}
		}));
	}

	/**
	 * Executes a client-sided command from a message.
	 *
	 * @param message the command message
	 * @return true if the message should not be sent to the server, false otherwise
	 */
	public static boolean executeCommand(String message) {
		if (message.isEmpty()) {
			return false; // Nothing to process
		}

		char prefix = message.charAt(0);
		CommandDispatcher<FabricClientCommandSource> dispatcher = getDispatcher(prefix);

		if (dispatcher == null) {
			return false; // Unknown prefix, won't execute anything.
		}

		MinecraftClient client = MinecraftClient.getInstance();

		// The interface is implemented on ClientCommandSource with a mixin.
		// noinspection ConstantConditions
		FabricClientCommandSource commandSource = (FabricClientCommandSource) client.getNetworkHandler().getCommandSource();

		client.getProfiler().push(message);

		try {
			dispatcher.execute(message.substring(1), commandSource);
			return true;
		} catch (CommandSyntaxException e) {
			LOGGER.warn("Syntax exception for client-sided command '{}'", message, e);

			if (isIgnoredException(e.getType())) {
				return false;
			}

			commandSource.sendError(getErrorMessage(e));
			return true;
		} catch (CommandException e) {
			LOGGER.warn("Error while executing client-sided command '{}'", message, e);
			commandSource.sendError(e.getTextMessage());
			return true;
		} catch (RuntimeException e) {
			LOGGER.warn("Error while executing client-sided command '{}'", message, e);
			commandSource.sendError(Text.of(e.getMessage()));
			return true;
		} finally {
			client.getProfiler().pop();
		}
	}

	/**
	 * Tests whether a command syntax exception with the type
	 * should be ignored and the message sent to the server.
	 *
	 * @param type the exception type
	 * @return true if ignored, false otherwise
	 */
	private static boolean isIgnoredException(CommandExceptionType type) {
		BuiltInExceptionProvider builtins = CommandSyntaxException.BUILT_IN_EXCEPTIONS;

		// Only ignore unknown commands and node parse exceptions.
		// The argument-related dispatcher exceptions are not ignored because
		// they will only happen if the user enters a correct command.
		return type == builtins.dispatcherUnknownCommand() || type == builtins.dispatcherParseException();
	}

	// See CommandSuggestor.method_30505. That cannot be used directly as it returns an OrderedText instead of a Text.
	private static Text getErrorMessage(CommandSyntaxException e) {
		Text message = Texts.toText(e.getRawMessage());
		String context = e.getContext();

		return context != null ? new TranslatableText("command.context.parse_error", message, context) : message;
	}

	/* @Nullable */
	public static CommandDispatcher<FabricClientCommandSource> getDispatcher(char prefix) {
		return dispatchers.get(prefix);
	}

	public static void buildDispatchers() {
		// This should only be called once at the end of the client constructor.
		if (!dispatchers.isEmpty()) {
			throw new IllegalStateException("Dispatchers have already been built!");
		}

		LOGGER.debug("Building client-side command dispatchers");

		for (char prefix : events.keySet()) {
			CommandDispatcher<FabricClientCommandSource> dispatcher = new CommandDispatcher<>();
			addCommands(prefix, dispatcher);
			// noinspection CodeBlock2Expr
			dispatcher.findAmbiguities((parent, child, sibling, inputs) -> {
				LOGGER.warn("Ambiguity between arguments {} and {} with inputs: {} (client-side command prefix: {})", dispatcher.getPath(child), dispatcher.getPath(sibling), inputs, prefix);
			});
			dispatchers.put(prefix, dispatcher);
		}
	}

	public static void addCommands(char prefix, CommandDispatcher<FabricClientCommandSource> dispatcher) {
		Event<ClientCommandRegistrationCallback> event = events.get(prefix);

		if (event != null) {
			event.invoker().register(dispatcher);
		}
	}

	public static boolean isInvalidCommandPrefix(char prefix) {
		return Character.isLetterOrDigit(prefix) || Character.isWhitespace(prefix);
	}

	public static boolean isPrefixUsed(char prefix) {
		return events.containsKey(prefix);
	}
}
