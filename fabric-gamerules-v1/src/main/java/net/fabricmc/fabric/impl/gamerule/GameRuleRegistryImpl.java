package net.fabricmc.fabric.impl.gamerule;

import net.fabricmc.fabric.api.gamerule.GameRuleRegistry;
import net.fabricmc.fabric.mixin.gamerule.MixinBooleanRule;
import net.fabricmc.fabric.mixin.gamerule.MixinGameRules;
import net.fabricmc.fabric.mixin.gamerule.MixinIntRule;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;

public class GameRuleRegistryImpl implements GameRuleRegistry {

	/**
	 * Register a new game rule.
	 * @param name a namespaced ID for your game rule, to prevent collision.
	 * @param ruleType The type of rule to register, made with createBooleanRule or createIntRule
	 * @param <T> Either BooleanRule or IntRule
	 * @return The key of your rule, to check {@link GameRules#getBoolean(GameRules.RuleKey)} or {@link GameRules#getInt(GameRules.RuleKey)} with.
	 */
	public <T extends GameRules.Rule<T>> GameRules.RuleKey<T> register(Identifier name, GameRules.RuleType<T> ruleType) {
		return MixinGameRules.callRegister(name.toString(), ruleType);
	}

	/**
	 * Create a new game rule with a boolean value.
	 * @param defaultValue The default value this rule should start out with.
	 * @return A RuleType to pass to register.
	 */
	public GameRules.RuleType<GameRules.BooleanRule> createBooleanRule(boolean defaultValue) {
		return MixinBooleanRule.callOf(defaultValue);
	}

	/**
	 * Create a new game rule with an int value.
	 * @param defaultValue The default value this rule should start out with.
	 * @return A RuleType to pass to register.
	 */
	public GameRules.RuleType<GameRules.IntRule> createIntRule(int defaultValue) {
		return MixinIntRule.callOf(defaultValue);
	}
}
