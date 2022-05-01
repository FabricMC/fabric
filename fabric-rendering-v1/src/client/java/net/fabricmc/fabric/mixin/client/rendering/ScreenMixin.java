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

package net.fabricmc.fabric.mixin.client.rendering;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;

import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;

@Mixin(Screen.class)
public class ScreenMixin {
	// Synthetic lambda body in renderTooltip
	@Inject(at = @At("HEAD"), method = "method_32635(Ljava/util/List;Lnet/minecraft/client/item/TooltipData;)V", cancellable = true)
	private static void injectRenderTooltipLambda(List<TooltipComponent> components, TooltipData data, CallbackInfo ci) {
		TooltipComponent component = TooltipComponentCallback.EVENT.invoker().getComponent(data);

		if (component != null) {
			components.add(1, component);
			ci.cancel();
		}
	}
}
