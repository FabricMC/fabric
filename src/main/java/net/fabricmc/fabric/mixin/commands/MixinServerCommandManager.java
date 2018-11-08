/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.mixin.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.commands.CommandRegistry;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.command.ServerCommandSource;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommandManager.class)
public class MixinServerCommandManager {
	@Shadow
	private static Logger LOGGER;
	@Shadow
	private CommandDispatcher<ServerCommandSource> dispatcher;

	@Inject(method = "<init>(Z)V", at = @At("RETURN"))
	public void addMethods(boolean dedicated, CallbackInfo info) {
		// TODO: Run before findAmbiguities
		CommandRegistry.INSTANCE.entries(false).forEach((e) -> e.accept(dispatcher));
		if (dedicated) {
			CommandRegistry.INSTANCE.entries(true).forEach((e) -> e.accept(dispatcher));
		}
	}
}
