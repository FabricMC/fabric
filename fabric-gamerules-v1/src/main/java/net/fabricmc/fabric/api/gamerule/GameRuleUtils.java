package net.fabricmc.fabric.api.gamerule;

import com.mojang.brigadier.arguments.ArgumentType;
import net.fabricmc.fabric.impl.gamerule.GameRuleUtilsImpl;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface GameRuleUtils {
	final GameRuleUtils INSTANCE = new GameRuleUtilsImpl();

	/**
	 * Register a new game rule.
	 * @param name a namespaced ID for your game rule, to prevent collision.
	 * @param ruleType The type of rule to register, made with createBooleanRule or createIntRule
	 * @param <T> Either BooleanRule or IntRule
	 * @return The key of your rule, to check {@link GameRules#getBoolean(GameRules.RuleKey)} or {@link GameRules#getInt(GameRules.RuleKey)} with.
	 */
	<T extends GameRules.Rule<T>> GameRules.RuleKey<T> register(Identifier name, GameRules.RuleType<T> ruleType);

	/**
	 * Create a new game rule with a boolean value.
	 * @param defaultValue The default value this rule should start out with.
	 * @return A RuleType to pass to register.
	 */
	GameRules.RuleType<GameRules.BooleanRule> createBooleanRule(boolean defaultValue);

	/**
	 * Create a new game rule with an int value.
	 * @param defaultValue The default value this rule should start out with.
	 * @return A RuleType to pass to register.
	 */
	GameRules.RuleType<GameRules.IntRule> createIntRule(int defaultValue);

	/**
	 * Create a new game rule with a custom value. Call this from an extension of {@link GameRules.Rule<T>}
	 * Used in place of the private {@link GameRules.RuleType<T>} constructor.
	 * @param argumentType The argument type to pass to this game rule from the /gamerule command.
	 * @param factory The factory for creating a new Rule with.
	 * @param notifier Called whenever the game rule is set.
	 * @param <T> The class to base the rule around. Must be parasable to/from a String.
	 * @return The new RuleType to pass into register.
	 */
	<T extends GameRules.Rule<T>> GameRules.RuleType<T> createCustomRule(Supplier<ArgumentType<?>> argumentType, Function<GameRules.RuleType<T>, T> factory, BiConsumer<MinecraftServer, T> notifier);

}
