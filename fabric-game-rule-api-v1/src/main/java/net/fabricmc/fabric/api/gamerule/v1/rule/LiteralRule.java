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

package net.fabricmc.fabric.api.gamerule.v1.rule;

import com.mojang.brigadier.context.CommandContext;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.GameRules;

/**
 * Represents a game rule which uses literals for the argument in commands.
 *
 * @param <T> the type of rule
 */
public abstract class LiteralRule<T extends LiteralRule<T>> extends GameRules.Rule<T> {
	protected LiteralRule(GameRules.Type<T> type) {
		super(type);
	}

	@Override
	protected final void setFromArgument(CommandContext<ServerCommandSource> context, String name) {
		// Do nothing. We use a different system for application with literals
	}
}
