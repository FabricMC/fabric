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

package net.fabricmc.fabric.impl.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Texts;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.text.TranslatableText;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.fabric.api.command.v1.ClientCommandManager;

public class ClientCommandManagerImpl implements ClientCommandManager {
	private static final Logger LOGGER = LogManager.getLogger();

	private final CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher<>();

	private static void sendError(Text text) {
		MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(
				new LiteralText("").append(text).formatted(Formatting.RED));
	}

	@Override
	public CommandDispatcher<CommandSource> getDispatcher() {
		return dispatcher;
	}

	@Override
	public <T> int execute(CommandDispatcher<T> dispatcher, ParseResults<T> parseResults) {
		MinecraftClient.getInstance().getProfiler().push(parseResults.getReader().getString());

		try {
			return dispatcher.execute(parseResults);
		} catch (CommandException e) {
			sendError(e.getTextMessage());
		} catch (CommandSyntaxException e) {
			sendError(Texts.toText(e.getRawMessage()));

			String input = e.getInput();

			if (input != null && e.getCursor() >= 0) {
				int i = Math.min(input.length(), e.getCursor());

				MutableText text = new LiteralText("")
						.formatted(Formatting.GRAY)
						.styled((style) -> style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, input)));

				if (i > 10) {
					text.append("...");
				}

				text.append(input.substring(Math.max(0, i - 10), i));

				if (i < input.length()) {
					text.append(new LiteralText(input.substring(i)).formatted(Formatting.RED, Formatting.UNDERLINE));
				}

				text.append((new TranslatableText("command.context.here")).formatted(Formatting.RED, Formatting.ITALIC));

				sendError(text);
			}
		} catch (Exception e) {
			MutableText text = new LiteralText(e.getMessage() == null ? e.getClass().getName() : e.getMessage());

			sendError(new TranslatableText("command.failed")
					.styled((style) -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, text))));

			if (LOGGER.isDebugEnabled()) {
				LOGGER.error("Command exception: {}", parseResults.getReader().getString(), e);

				StackTraceElement[] stackTraceElements = e.getStackTrace();

				for (int j = 0; j < Math.min(stackTraceElements.length, 3); ++j) {
					text.append("\n\n")
							.append(stackTraceElements[j].getMethodName())
							.append("\n ")
							.append(String.valueOf(stackTraceElements[j].getFileName()))
							.append(":")
							.append(String.valueOf(stackTraceElements[j].getLineNumber()));
				}
			}

			if (SharedConstants.isDevelopment) {
				LOGGER.error("'" + parseResults.getReader().getString() + "' threw an exception", e);

				sendError(new LiteralText(Util.getInnermostMessage(e)));
			}
		}

		MinecraftClient.getInstance().getProfiler().pop();

		return 0;
	}
}
