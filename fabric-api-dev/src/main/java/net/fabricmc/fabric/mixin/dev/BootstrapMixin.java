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

package net.fabricmc.fabric.mixin.dev;

import java.util.Set;
import java.util.function.Consumer;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.Bootstrap;

import net.fabricmc.fabric.api.FabricDevProperties;

@Mixin(Bootstrap.class)
public class BootstrapMixin {
	@ModifyExpressionValue(method = "logMissing", at = @At(value = "FIELD", target = "Lnet/minecraft/SharedConstants;isDevelopment:Z"))
	private static boolean isDevelopmentForDevModule(boolean original) {
		return original || FabricDevProperties.LOG_MISSING_TRANSLATIONS || FabricDevProperties.ENABLE_COMMAND_ARGUMENT_LOGGING;
	}

	@WrapWithCondition(method = "logMissing", at = @At(value = "INVOKE", target = "Ljava/util/Set;forEach(Ljava/util/function/Consumer;)V"))
	private static boolean wrapWithConditionTranslationWarnings(Set<String> instance, Consumer<String> consumer) {
		return FabricDevProperties.LOG_MISSING_TRANSLATIONS;
	}

	@WrapWithCondition(method = "logMissing", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/CommandManager;checkMissing()V"))
	private static boolean wrapWithConditionCommandArgumentWarnings() {
		return FabricDevProperties.ENABLE_COMMAND_ARGUMENT_LOGGING;
	}
}
