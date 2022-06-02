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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.message.v1.ServerMessageDecoratorEvent;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;

public class ChatTest implements ModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChatTest.class);

	@Override
	public void onInitialize() {
		Executor ioWorkerExecutor = Util.getIoWorkerExecutor();

		// Basic content phase testing
		ServerMessageDecoratorEvent.EVENT.register(ServerMessageDecoratorEvent.CONTENT_PHASE, (sender, message) -> {
			if (message.getString().contains("tater")) {
				return CompletableFuture.completedFuture(message.copy().append(" :tiny_potato:"));
			}

			return CompletableFuture.completedFuture(message);
		});

		// Basic styling phase testing
		ServerMessageDecoratorEvent.EVENT.register(ServerMessageDecoratorEvent.STYLING_PHASE, (sender, message) -> {
			if (sender != null && sender.getAbilities().creativeMode) {
				return CompletableFuture.completedFuture(message.copy().styled(style -> style.withColor(0xFFA500)));
			}

			return CompletableFuture.completedFuture(message);
		});

		// Async testing
		ServerMessageDecoratorEvent.EVENT.register(ServerMessageDecoratorEvent.CONTENT_PHASE, (sender, message) -> {
			if (message.getString().contains("wait")) {
				return CompletableFuture.supplyAsync(() -> {
					try {
						Thread.sleep(Random.create().nextBetween(500, 2000));
					} catch (InterruptedException ignored) {
						// Ignore interruption
					}

					return message;
				}, ioWorkerExecutor);
			}

			return CompletableFuture.completedFuture(message);
		});

		// ServerMessageEvents
		ServerMessageEvents.CHAT_MESSAGE.register(
				(message, sender, typeKey) -> LOGGER.info("ChatTest: {} sent \"{}\"", sender, message)
		);
		ServerMessageEvents.GAME_MESSAGE.register(
				(message, typeKey) -> LOGGER.info("ChatTest: server sent \"{}\"", message)
		);
		ServerMessageEvents.COMMAND_MESSAGE.register(
				(message, source, typeKey) -> LOGGER.info("ChatTest: command sent \"{}\"", message)
		);

		// ServerMessageEvents blocking
		ServerMessageEvents.ALLOW_CHAT_MESSAGE.register(
				(message, sender, typeKey) -> !message.raw().getContent().getString().contains("sadtater")
		);
		ServerMessageEvents.ALLOW_GAME_MESSAGE.register((message, typeKey) -> {
			if (message.getContent() instanceof TranslatableTextContent translatable) {
				return !translatable.getKey().startsWith("death.attack.badRespawnPoint.");
			}

			return true;
		});
		ServerMessageEvents.ALLOW_COMMAND_MESSAGE.register(
				(message, source, typeKey) -> !message.raw().getContent().getString().contains("sadtater")
		);
	}
}
