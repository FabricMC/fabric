/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.fabricmc.fabric.mixin.eventsgui;

import net.fabricmc.fabric.api.client.event.gui.ScreenInitCallback;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.render.item.ItemRenderer;

import java.util.List;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Lists;

@Mixin(Screen.class)
public abstract class MixinScreen {
	
	@Accessor("buttons")
	List<AbstractButtonWidget> buttons();

	@Shadow
	protected final List<AbstractButtonWidget> buttons;

	@Shadow
	protected abstract <T extends AbstractButtonWidget> T addButton(T abstractButtonWidget_1);

	@Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At("RETURN"))
	private void onInit(MinecraftClient minecraftClient_1, int w, int h, CallbackInfo ci) {
		ScreenInitCallback.EVENT.invoker().init((Screen) (Object) this, new ButtonList((Screen) (Object) this));
	}
}
