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

public class FloatRule extends GameRules.Rule<FloatRule> implements ValidateableRule {
	private static final Logger LOGGER = LogManager.getLogger(GameRuleRegistry.class);

	private final float lowerBound;
	private final float upperBound;
	private float value;

	@Deprecated
	public FloatRule(GameRules.RuleType<FloatRule> type, float value, float lowerBound, float upperBound) {
		super(type);
		this.value = value;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	@Override
	protected void setFromArgument(CommandContext<ServerCommandSource> context, String name) {
		this.value = context.getArgument(name, Float.class);
	}

	@Override
	protected void deserialize(String value) {
		final float f = FloatRule.parseFloat(value);

		if (this.lowerBound > f || this.upperBound < f) {
			LOGGER.warn("Failed to parse float {}. Was out of bounds {} - {}", value, this.lowerBound, this.upperBound);
			return;
		}

		this.value = f;
	}

	private static float parseFloat(String string) {
		if (!string.isEmpty()) {
			try {
				return Float.parseFloat(string);
			} catch (NumberFormatException e) {
				LOGGER.warn("Failed to parse float {}", string);
			}
		}

		return 0.0F;
	}

	@Override
	public String serialize() {
		return Float.toString(this.value);
	}

	@Override
	public int getCommandResult() {
		return 0;
	}

	@Override
	protected FloatRule getThis() {
		return this;
	}

	@Override
	protected FloatRule copy() {
		return new FloatRule(this.type, this.value, this.lowerBound, this.upperBound);
	}

	@Override
	public void setValue(FloatRule rule, MinecraftServer minecraftServer) {
		this.value = rule.value;
		this.changed(minecraftServer);
	}

	@Override
	public boolean validate(String value) {
		try {
			final float f = Float.parseFloat(value);

			return !(this.lowerBound > f) && !(this.upperBound < f);
		} catch (NumberFormatException ignored) {
			return false;
		}
	}

	public float get() {
		return this.value;
	}
}
