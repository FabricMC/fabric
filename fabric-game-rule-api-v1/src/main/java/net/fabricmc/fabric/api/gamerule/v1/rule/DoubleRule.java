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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.GameRules;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;

public final class DoubleRule extends GameRules.Rule<DoubleRule> implements ValidateableRule {
	private static final Logger LOGGER = LogManager.getLogger(GameRuleRegistry.class);

	private final double minimumValue;
	private final double maximumValue;
	private double value;

	/**
	 * @deprecated You should not be calling this constructor!
	 */
	@Deprecated
	public DoubleRule(GameRules.Type<DoubleRule> type, double value, double minimumValue, double maximumValue) {
		super(type);
		this.value = value;
		this.minimumValue = minimumValue;
		this.maximumValue = maximumValue;
	}

	@Override
	protected void setFromArgument(CommandContext<ServerCommandSource> context, String name) {
		this.value = context.getArgument(name, Double.class);
	}

	@Override
	protected void deserialize(String value) {
		final double d = DoubleRule.parseDouble(value);

		if (this.minimumValue > d || this.maximumValue < d) {
			LOGGER.warn("Failed to parse double {}. Was out of bounds {} - {}", value, this.minimumValue, this.maximumValue);
			return;
		}

		this.value = d;
	}

	private static double parseDouble(String string) {
		if (!string.isEmpty()) {
			try {
				return Double.parseDouble(string);
			} catch (NumberFormatException e) {
				LOGGER.warn("Failed to parse double {}", string);
			}
		}

		return 0.0D;
	}

	@Override
	public String serialize() {
		return Double.toString(this.value);
	}

	@Override
	public int getCommandResult() {
		return Double.compare(this.value, 0.0);
	}

	@Override
	protected DoubleRule getThis() {
		return this;
	}

	@Override
	protected DoubleRule copy() {
		return new DoubleRule(this.type, this.value, this.minimumValue, this.maximumValue);
	}

	@Override
	public void setValue(DoubleRule rule, MinecraftServer minecraftServer) {
		this.value = rule.value;
		this.changed(minecraftServer);
	}

	@Override
	public boolean validate(String value) {
		try {
			final double d = Double.parseDouble(value);

			return !(this.minimumValue > d) && !(this.maximumValue < d);
		} catch (NumberFormatException ignored) {
			return false;
		}
	}

	public double get() {
		return this.value;
	}
}
