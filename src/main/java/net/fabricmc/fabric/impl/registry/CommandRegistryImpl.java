/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.impl.registry;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.CommandRegistry;
import net.fabricmc.fabric.api.command.CommandType;
import net.minecraft.server.command.ServerCommandSource;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandRegistryImpl implements CommandRegistry {

	public static final CommandRegistryImpl INSTANCE = new CommandRegistryImpl();

	private final Map<CommandType, List<Consumer<CommandDispatcher<ServerCommandSource>>>> commands = new HashMap<>();

	public List<Consumer<CommandDispatcher<ServerCommandSource>>> entries(CommandType... type) {
		return Arrays.stream(type)
			.filter(commands::containsKey)
			.map(commands::get)
			.flatMap((Function<List<Consumer<CommandDispatcher<ServerCommandSource>>>, Stream<Consumer<CommandDispatcher<ServerCommandSource>>>>) Collection::stream)
			.collect(Collectors.toList());
	}

	public void register(CommandType type, Consumer<CommandDispatcher<ServerCommandSource>> consumer) {
		commands.putIfAbsent(type, new ArrayList<>());
		commands.get(type).add(consumer);
	}
}
