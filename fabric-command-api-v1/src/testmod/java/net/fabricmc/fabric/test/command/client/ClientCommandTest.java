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

package net.fabricmc.fabric.test.command.client;

import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

import net.minecraft.text.LiteralText;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientArgumentBuilders;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandRegistrationCallback;

public final class ClientCommandTest implements ClientModInitializer {
	private static final DynamicCommandExceptionType IS_NULL = new DynamicCommandExceptionType(x -> new LiteralText("The " + x + " is null"));

	@Override
	public void onInitializeClient() {
		ClientCommandRegistrationCallback.event().register(registerSimpleCommands("This is a client command"));
		// Using `\` as prefix
		ClientCommandRegistrationCallback.event('\\').register(registerSimpleCommands("This is a client command with backslashes"));
	}

	private static ClientCommandRegistrationCallback registerSimpleCommands(String message) {
		return dispatcher -> {
			dispatcher.register(ClientArgumentBuilders.literal("test-client-cmd").executes(context -> {
				context.getSource().sendFeedback(new LiteralText(message));

				if (context.getSource().getClient() == null) {
					throw IS_NULL.create("client");
				}

				if (context.getSource().getWorld() == null) {
					throw IS_NULL.create("world");
				}

				if (context.getSource().getPlayer() == null) {
					throw IS_NULL.create("player");
				}

				return 0;
			}));
		};
	}
}
