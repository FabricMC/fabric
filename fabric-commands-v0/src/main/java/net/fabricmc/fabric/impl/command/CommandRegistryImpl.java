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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.server.command.ServerCommandSource;

public class CommandRegistryImpl {
	public static final CommandRegistryImpl INSTANCE = new CommandRegistryImpl();

	private final List<Consumer<CommandDispatcher<ServerCommandSource>>> serverCommands;
	private final List<Consumer<CommandDispatcher<ServerCommandSource>>> dedicatedServerCommands;

	public CommandRegistryImpl() {
		this.serverCommands = new ArrayList<>();
		this.dedicatedServerCommands = new ArrayList<>();
	}

	public List<Consumer<CommandDispatcher<ServerCommandSource>>> entries(boolean dedicated) {
		return Collections.unmodifiableList(dedicated ? dedicatedServerCommands : serverCommands);
	}

	public void register(boolean dedicated, Consumer<CommandDispatcher<ServerCommandSource>> consumer) {
		if (dedicated) {
			dedicatedServerCommands.add(consumer);
		} else {
			serverCommands.add(consumer);
		}
	}
}
