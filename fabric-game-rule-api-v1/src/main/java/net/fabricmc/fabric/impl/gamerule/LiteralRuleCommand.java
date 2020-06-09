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

package net.fabricmc.fabric.impl.gamerule;

import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.GameRules;

import net.fabricmc.fabric.api.gamerule.v1.rule.LiteralRule;
import net.fabricmc.fabric.mixin.gamerule.GameRuleCommandAccessor;

public final class LiteralRuleCommand {
	private LiteralRuleCommand() {
	}

	public static <T extends LiteralRule<T>> void register(LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder, GameRules.Key<T> key, LiteralRuleType<T> type) {
		literalArgumentBuilder.then(literal(key.getName())
				.executes(context -> {
					// We can use the vanilla query method
					return GameRuleCommandAccessor.invokeExecuteQuery(context.getSource(), key);
				}
		));

		// The LiteralRuleType handles the executeSet
		type.register(literalArgumentBuilder, key);
	}
}
