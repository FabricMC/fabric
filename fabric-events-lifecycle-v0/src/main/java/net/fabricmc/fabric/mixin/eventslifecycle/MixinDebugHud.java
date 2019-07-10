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

package net.fabricmc.fabric.mixin.eventslifecycle;

import net.fabricmc.fabric.api.event.client.DebugHudCallback;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(DebugHud.class)
public class MixinDebugHud {

	//This injects after the the keybinds has been drawn
	@Inject(at = @At(value = "INVOKE_ASSIGN", target = "java/util/List.add(Ljava/lang/Object;)Z", ordinal = 2), method = "drawLeftText", locals = LocalCapture.CAPTURE_FAILHARD)
	private void drawLeftText(CallbackInfo info, List<String> list) {
		DebugHudCallback.EVENT_LEFT.invoker().debugHudText(list);
	}

	@Inject(at = @At("RETURN"), method = "getRightText")
	private void getRightText(CallbackInfoReturnable<List<String>> info) {
		DebugHudCallback.EVENT_RIGHT.invoker().debugHudText(info.getReturnValue());
	}

}
