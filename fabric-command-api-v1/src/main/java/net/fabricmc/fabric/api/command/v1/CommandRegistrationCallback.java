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

import net.minecraft.server.command.ServerCommandSource;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Callback for when a server registers all commands.
 *
 * <p>To register some commands, you would register an event listener and implement the callback.
 *
 * <pre><code>
 * CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
 *     // For example, this command is only registered on an integrated server
 *     if (!dedicated) dispatcher.register(CommandManager.literal("integrated_command").executes(context -> {...}));
 * })};
 * </code></pre>
 */
public interface CommandRegistrationCallback {
	Event<CommandRegistrationCallback> EVENT = EventFactory.createArrayBacked(CommandRegistrationCallback.class, (callbacks) -> (dispatcher, dedicated) -> {
		for (CommandRegistrationCallback callback : callbacks) {
			callback.register(dispatcher, dedicated);
		}
	});

	/**
	 * Called when the server is registering commands.
	 *
	 * @param dispatcher the command dispatcher to register commands to.
	 * @param dedicated whether the server this command is being registered on is a dedicated server.
	 */
	void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated);
}
