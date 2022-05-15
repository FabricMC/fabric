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

package net.fabricmc.fabric.mixin.chat;

import net.fabricmc.fabric.impl.chat.ChatDecoratorInternals;

import net.minecraft.class_7492;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({MinecraftServer.class, MinecraftDedicatedServer.class})
public class MinecraftServerMixin {
	@Inject(method = "method_43929", at = @At("RETURN"), cancellable = true)
	private void onGetChatDecorator(CallbackInfoReturnable<class_7492> cir) {
		class_7492 originalDecorator = cir.getReturnValue();
		cir.setReturnValue((sender, message) -> ChatDecoratorInternals.decorate(sender, originalDecorator.decorate(sender, message)));
	}
}
