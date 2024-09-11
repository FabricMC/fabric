package net.fabricmc.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import net.fabricmc.fabric.FabricDev;

import net.minecraft.Bootstrap;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;
import java.util.function.Consumer;

@Mixin(Bootstrap.class)
public class BootstrapMixin {
	@Dynamic("@ModifyExpressionValue's the FIELD GET of SharedConstants.isDevelopment to add OR conditions for FabricDev.LOG_MISSING_TRANSLATIONS and FabricDev.ENABLE_COMMAND_ARGUMENT_LOGGING")
	@ModifyExpressionValue(method = "logMissing", at = @At(value = "FIELD", target = "Lnet/minecraft/SharedConstants;isDevelopment:Z"))
	private static boolean fabric$mevIsDevelopmentForDevModule(boolean original) {
		return original || FabricDev.LOG_MISSING_TRANSLATIONS || FabricDev.ENABLE_COMMAND_ARGUMENT_LOGGING;
	}

	@Dynamic("Only execute the forEach if FabricDev.LOG_MISSING_TRANSLATIONS is true")
	@WrapWithCondition(method = "logMissing", at = @At(value = "INVOKE", target = "Ljava/util/Set;forEach(Ljava/util/function/Consumer;)V"))
	private static boolean fabric$wrapWithConditionTranslationWarnings(Set<String> instance, Consumer<String> consumer) {
		return FabricDev.LOG_MISSING_TRANSLATIONS;
	}

	@Dynamic("Only log command argument exceptions if FabricDev.ENABLE_COMMAND_ARGUMENT_LOGGING is true")
	@WrapWithCondition(method = "logMissing", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/CommandManager;checkMissing()V"))
	private static boolean fabric$wrapWithConditionCommandArgumentWarnings() {
		return FabricDev.ENABLE_COMMAND_ARGUMENT_LOGGING;
	}
}
