package net.fabricmc.fabric.mixin.gamerule;

import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRules.IntRule.class)
public interface MixinIntRule {
	@SuppressWarnings("PublicStaticMixinMember")
	@Invoker
	static GameRules.RuleType<GameRules.IntRule> callOf(int defaultValue) {
		throw new AssertionError("Mixin to GameRules$IntRule failed!");
	}
}
