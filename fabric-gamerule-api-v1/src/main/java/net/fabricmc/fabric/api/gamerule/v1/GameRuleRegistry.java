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

import net.fabricmc.fabric.impl.gamerule.RuleKeyInternals;
import net.fabricmc.fabric.mixin.gamerule.GameRulesAccessor;

public final class GameRuleRegistry {
	private GameRuleRegistry() {
	}

	/**
	 * Registers a {@link GameRules.Rule}.
	 *
	 * @param name   the name of the rule
	 * @param category the category of this rule
	 * @param type the rule type
	 * @param <T>  the type of rule
	 * @return a rule key which can be used to query the value of the rule
	 * @throws IllegalStateException if a rule of the same name already exists
	 */
	public static <T extends GameRules.Rule<T>> GameRules.RuleKey<T> register(String name, GameRules.RuleCategory category, GameRules.RuleType<T> type) {
		return GameRulesAccessor.invokeRegister(name, category, type);
	}

	/**
	 * Registers a {@link GameRules.Rule} with a custom category.
	 *
	 * @param name 	the name of the rule
	 * @param category the category of this rule
	 * @param type the rule type
	 * @param <T>  the type of rule
	 * @return a rule key which can be used to query the value of the rule
	 * @throws IllegalStateException if a rule of the same name already exists
	 */
	public static <T extends GameRules.Rule<T>> GameRules.RuleKey<T> register(String name, CustomGameRuleCategory category, GameRules.RuleType<T> type) {
		final GameRules.RuleKey<T> key = GameRulesAccessor.invokeRegister(name, GameRules.RuleCategory.MISC, type);
		((RuleKeyInternals) (Object) key).fabric_setCustomCategory(category);
		return key;
	}

	/**
	 * Checks if a name for a rule is already being used.
	 *
	 * @param ruleName the rule name to test
	 * @return true if the name is taken.
	 */
	public static boolean isRuleNameUsed(String ruleName) {
		return GameRulesAccessor.getRuleTypes().keySet().stream().anyMatch(key -> key.getName().equals(ruleName));
	}
}

