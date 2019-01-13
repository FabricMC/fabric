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

package net.fabricmc.fabric.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class CommandRegistry {
	public static final CommandRegistry INSTANCE = new CommandRegistry();

	private final List<Consumer<CommandDispatcher<ServerCommandSource>>> serverCommands;
	private final List<Consumer<CommandDispatcher<ServerCommandSource>>> dedicatedServerCommands;
	private final List<Consumer<CommandDispatcher<ServerCommandSource>>> clientCommands;

	protected CommandRegistry() {
		this.serverCommands = new ArrayList<>();
		this.dedicatedServerCommands = new ArrayList<>();
		this.clientCommands = new ArrayList<>();
	}

	@Deprecated
	public List<Consumer<CommandDispatcher<ServerCommandSource>>> entries(boolean dedicated) {
		return Collections.unmodifiableList(dedicated ? dedicatedServerCommands : serverCommands);
	}

	public List<Consumer<CommandDispatcher<ServerCommandSource>>> serverEntries() {
		return Collections.unmodifiableList(serverCommands);
	}

	public List<Consumer<CommandDispatcher<ServerCommandSource>>> dedicatedServerEntries() {
		return Collections.unmodifiableList(dedicatedServerCommands);
	}

	public List<Consumer<CommandDispatcher<ServerCommandSource>>> clientEntries() {
		return Collections.unmodifiableList(clientCommands);
	}

	@Deprecated
	public void register(boolean dedicated, Consumer<CommandDispatcher<ServerCommandSource>> consumer) {
		if (dedicated) {
			dedicatedServerCommands.add(consumer);
		} else {
			serverCommands.add(consumer);
		}
	}

	public void register(Side side, Consumer<CommandDispatcher<ServerCommandSource>> consumer) {
		switch (side) {
			case SERVER:
				serverCommands.add(consumer);
				break;
			case CLIENT:
				clientCommands.add(consumer);
				break;
			case DEDICATED_SERVER:
				dedicatedServerCommands.add(consumer);
				break;
		}
	}

	public enum Side {
		SERVER, DEDICATED_SERVER, CLIENT
	}
}
