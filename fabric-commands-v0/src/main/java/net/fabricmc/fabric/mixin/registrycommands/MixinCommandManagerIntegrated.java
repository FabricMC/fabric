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

package net.fabricmc.fabric.mixin.registrycommands;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.impl.registry.CommandRegistryImpl;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandManager.class)
public class MixinCommandManagerIntegrated {
	@Shadow
	private CommandDispatcher<ServerCommandSource> dispatcher;

	@Inject(method = "<init>(Z)V", at = @At("RETURN"))
	public void addMethods(boolean dedicated, CallbackInfo info) {
		// TODO: Run before findAmbiguities
		if (!dedicated) {
			CommandRegistryImpl.INSTANCE.entries(false).forEach((e) -> e.accept(dispatcher));
		}
	}
}
