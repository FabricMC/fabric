package net.fabricmc.fabric.api.gamerule;

import net.fabricmc.fabric.impl.gamerule.GameRuleRegistryImpl;

public interface GameRuleRegistry {
	final GameRuleRegistry INSTANCE = new GameRuleRegistryImpl();
}
