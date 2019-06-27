package net.fabricmc.fabric.mixin.gamerule;

import net.minecraft.world.GameRules;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRules.class)
public interface MixinGameRules {
	@SuppressWarnings("PublicStaticMixinMember")
	@Invoker
	static <T extends GameRules.Rule<T>> GameRules.RuleKey<T> callRegister(String name, GameRules.RuleType<T> ruleType) {
		throw new NotImplementedException("Mixin to GameRules failed!");
	}
}
