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

package net.fabricmc.fabric.api.gamerule.v1;

import net.minecraft.world.GameRules;

import net.fabricmc.fabric.api.gamerule.v1.rule.DoubleRule;
import net.fabricmc.fabric.api.gamerule.v1.rule.EnumRule;
import net.fabricmc.fabric.api.gamerule.v1.rule.FloatRule;

/**
 * An extended RuleTypeConsumer which supports fabric's own rule types.
 */
public interface FabricRuleTypeConsumer extends GameRules.TypeConsumer {
	default void acceptDoubleRule(GameRules.Key<DoubleRule> key, GameRules.Type<DoubleRule> type) {
	}

	default void acceptFloatRule(GameRules.Key<FloatRule> key, GameRules.Type<FloatRule> type) {
	}

	default <E extends Enum<E>> void acceptEnumRule(GameRules.Key<EnumRule<E>> key, GameRules.Type<EnumRule<E>> type) {
	}
}
