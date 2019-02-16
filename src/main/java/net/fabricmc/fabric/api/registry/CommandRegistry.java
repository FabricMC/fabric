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

package net.fabricmc.fabric.api.registry;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Registry for server-side command providers.
 */
public class CommandRegistry {
	public static final CommandRegistry INSTANCE = new CommandRegistry();

	private final List<Consumer<CommandDispatcher<ServerCommandSource>>> serverCommands;
	private final List<Consumer<CommandDispatcher<ServerCommandSource>>> dedicatedServerCommands;

	protected CommandRegistry() {
		this.serverCommands = new ArrayList<>();
		this.dedicatedServerCommands = new ArrayList<>();
	}

	/**
	 * @deprecated Will be removed in 0.3.0; should not have been exposed.
	 */
	@Deprecated
	public List<Consumer<CommandDispatcher<ServerCommandSource>>> entries(boolean dedicated) {
		return Collections.unmodifiableList(dedicated ? dedicatedServerCommands : serverCommands);
	}

	/**
	 * Register a command provider.
	 * @param dedicated If true, the command is only registered on the dedicated server.
	 * @param consumer The command provider, consuming {@link CommandDispatcher}.
	 */
	public void register(boolean dedicated, Consumer<CommandDispatcher<ServerCommandSource>> consumer) {
		if (dedicated) {
			dedicatedServerCommands.add(consumer);
		} else {
			serverCommands.add(consumer);
		}
	}
}
