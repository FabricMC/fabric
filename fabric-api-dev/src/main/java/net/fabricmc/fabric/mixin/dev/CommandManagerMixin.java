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

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.server.command.CommandManager;

import net.fabricmc.fabric.impl.FabricDevProperties;

@Mixin(CommandManager.class)
public class CommandManagerMixin {
	@ModifyExpressionValue(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/SharedConstants;isDevelopment:Z"))
	private static boolean mevIsDevelopmentForDevModule(boolean original) {
		return original || FabricDevProperties.REGISTER_DEBUG_COMMANDS;
	}

	@ModifyExpressionValue(method = "execute", at = @At(value = "FIELD", target = "Lnet/minecraft/SharedConstants;isDevelopment:Z"))
	private static boolean mevIsDevelopmentForDevModule2(boolean original) {
		return original || FabricDevProperties.ENABLE_COMMAND_EXCEPTION_LOGGING;
	}
}
