package net.fabricmc.fabric.mixin.gamerule;

import net.minecraft.world.GameRules;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRules.BooleanRule.class)
public interface MixinBooleanRule {
	@SuppressWarnings("PublicStaticMixinMember")
	@Invoker
	static GameRules.RuleType<GameRules.BooleanRule> callOf(boolean defaultValue) {
		throw new NotImplementedException("Mixin to GameRules$BooleanRule Failed!");
	}
}
