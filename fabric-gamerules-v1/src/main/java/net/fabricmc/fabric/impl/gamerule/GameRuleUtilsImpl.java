package net.fabricmc.fabric.impl.gamerule;

import com.mojang.brigadier.arguments.ArgumentType;
import net.fabricmc.fabric.api.gamerule.GameRuleUtils;
import net.fabricmc.fabric.mixin.gamerule.MixinBooleanRule;
import net.fabricmc.fabric.mixin.gamerule.MixinGameRules;
import net.fabricmc.fabric.mixin.gamerule.MixinIntRule;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;

import java.lang.reflect.InvocationTargetException;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class GameRuleUtilsImpl implements GameRuleUtils {

	@Override
	public <T extends GameRules.Rule<T>> GameRules.RuleKey<T> register(Identifier name, GameRules.RuleType<T> ruleType) {
		return MixinGameRules.callRegister(name.toString(), ruleType);
	}

	@Override
	public GameRules.RuleType<GameRules.BooleanRule> createBooleanRule(boolean defaultValue) {
		return MixinBooleanRule.callOf(defaultValue);
	}

	@Override
	public GameRules.RuleType<GameRules.IntRule> createIntRule(int defaultValue) {
		return MixinIntRule.callOf(defaultValue);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends GameRules.Rule<T>> GameRules.RuleType<T> createCustomRule(Supplier<ArgumentType<?>> argumentType, Function<GameRules.RuleType<T>, T> factory, BiConsumer<MinecraftServer, T> notifier) {
		try {
			java.lang.reflect.Constructor<GameRules.RuleType> constructor = GameRules.RuleType.class.getDeclaredConstructor(Supplier.class, Function.class, BiConsumer.class);
			constructor.setAccessible(true);
			return constructor.newInstance(argumentType, factory, notifier);
		} catch (IllegalAccessException | InstantiationException | ClassCastException | NoSuchMethodException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}
