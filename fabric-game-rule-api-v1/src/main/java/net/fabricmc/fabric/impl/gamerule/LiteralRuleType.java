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

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.GameRules;

import net.fabricmc.fabric.api.gamerule.v1.rule.LiteralRule;

public abstract class LiteralRuleType<T extends LiteralRule<T>> extends GameRules.Type<T> {
	public LiteralRuleType(Supplier<ArgumentType<?>> argumentType, Function<GameRules.Type<T>, T> ruleFactory, BiConsumer<MinecraftServer, T> changeCallback, GameRules.Acceptor<T> acceptor) {
		super(argumentType, ruleFactory, changeCallback, acceptor);
	}

	@Override
	@Deprecated
	public final RequiredArgumentBuilder<ServerCommandSource, ?> argument(String name) {
		return super.argument(name);
	}

	/**
	 * Literal Rule types should implement this method in order to register their nodes on the GameRule command.
	 */
	public abstract void register(LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder, GameRules.Key<T> key);
}
