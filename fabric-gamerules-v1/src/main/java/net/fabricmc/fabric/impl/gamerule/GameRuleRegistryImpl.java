package net.fabricmc.fabric.impl.gamerule;

import net.fabricmc.fabric.api.gamerule.GameRuleRegistry;
import net.fabricmc.fabric.mixin.gamerule.MixinBooleanRule;
import net.fabricmc.fabric.mixin.gamerule.MixinGameRules;
import net.fabricmc.fabric.mixin.gamerule.MixinIntRule;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;

public class GameRuleRegistryImpl implements GameRuleRegistry {

	public <T extends GameRules.Rule<T>> GameRules.RuleKey<T> register(Identifier name, GameRules.RuleType<T> ruleType) {
		return MixinGameRules.callRegister(name.toString(), ruleType);
	}

	public GameRules.RuleType<GameRules.BooleanRule> createBooleanRule(boolean defaultValue) {
		return MixinBooleanRule.callOf(defaultValue);
	}

	public GameRules.RuleType<GameRules.IntRule> createIntRule(int defaultValue) {
		return MixinIntRule.callOf(defaultValue);
	}
}
