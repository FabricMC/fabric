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

package net.fabricmc.fabric.api.command.v1;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;

import net.fabricmc.fabric.impl.command.ClientCommandManagerImpl;

import net.minecraft.command.CommandSource;

public interface ClientCommandManager {
	ClientCommandManager INSTANCE = new ClientCommandManagerImpl();

	static LiteralArgumentBuilder<CommandSource> literal(String name) {
		return LiteralArgumentBuilder.literal(name);
	}

	static <T> RequiredArgumentBuilder<CommandSource, T> argument(String name, ArgumentType<T> argumentType) {
		return RequiredArgumentBuilder.argument(name, argumentType);
	}

	CommandDispatcher<CommandSource> getDispatcher();

	default <T> int execute(CommandDispatcher<T> dispatcher, String command, T source) {
		return execute(dispatcher, new StringReader(command), source);
	}

	default <T> int execute(CommandDispatcher<T> dispatcher, StringReader command, T source) {
		if (command.canRead(1) && command.peek() == '/') {
			command.skip();
		}

		return execute(dispatcher, dispatcher.parse(command, source));
	}

	<T> int execute(CommandDispatcher<T> dispatcher, ParseResults<T> parseResults);
}
