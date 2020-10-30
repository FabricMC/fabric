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

import java.util.Objects;

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

@Environment(EnvType.CLIENT)
public final class ClientCommandInternals {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final char PREFIX = '/';

	private static CommandDispatcher<FabricClientCommandSource> dispatcher = null;

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

		if (message.charAt(0) != PREFIX) {
			return false; // Incorrect prefix, won't execute anything.
		}

		CommandDispatcher<FabricClientCommandSource> dispatcher = getDispatcher();
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

	public static CommandDispatcher<FabricClientCommandSource> getDispatcher() {
		return Objects.requireNonNull(dispatcher, "Client command dispatcher not built!");
	}

	public static void buildDispatchers() {
		// This should only be called once at the end of the client constructor.
		if (dispatcher != null) {
			throw new IllegalStateException("Dispatchers have already been built!");
		}

		LOGGER.debug("Building client-side command dispatcher");

		dispatcher = new CommandDispatcher<>();
		addCommands(dispatcher);
		// noinspection CodeBlock2Expr
		dispatcher.findAmbiguities((parent, child, sibling, inputs) -> {
			LOGGER.warn("Ambiguity between arguments {} and {} with inputs: {}", dispatcher.getPath(child), dispatcher.getPath(sibling), inputs);
		});
	}

	public static void addCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {
		ClientCommandRegistrationCallback.EVENT.invoker().register(dispatcher);
	}
}
