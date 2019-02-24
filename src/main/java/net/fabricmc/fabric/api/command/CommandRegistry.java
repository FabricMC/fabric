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

package net.fabricmc.fabric.api.command;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.impl.registry.CommandRegistryImpl;
import net.minecraft.server.command.ServerCommandSource;

import java.util.function.Consumer;

/**
 * Registry for server-side command providers.
 */
public interface CommandRegistry {
	CommandRegistry INSTANCE = CommandRegistryImpl.INSTANCE;

	/**
	 * Register a command provider.
	 * @param type the type of command that will be registered
	 * @param consumer The command provider, consuming {@link CommandDispatcher}.
	 */
	void register(CommandType type, Consumer<CommandDispatcher<ServerCommandSource>> consumer);

	/**
	 * Register a command provider on all servers
	 * @param consumer The command provider, consuming {@link CommandDispatcher}.
	 */
	default void register(Consumer<CommandDispatcher<ServerCommandSource>> consumer) {
		register(CommandType.DEFAULT, consumer);
	}
}
