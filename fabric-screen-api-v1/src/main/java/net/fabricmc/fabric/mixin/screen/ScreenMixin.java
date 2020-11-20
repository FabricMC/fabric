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

package net.fabricmc.fabric.mixin.screen;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.impl.client.screen.ScreenExtensions;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.impl.client.screen.ButtonList;
import net.fabricmc.fabric.impl.client.screen.ScreenEventFactory;

@Mixin(Screen.class)
abstract class ScreenMixin implements ScreenExtensions {
	@Shadow
	@Final
	protected List<AbstractButtonWidget> buttons;
	@Shadow
	@Final
	protected List<Element> children;

	@Unique
	private ButtonList<AbstractButtonWidget> fabricButtons;
	@Unique
	private Event<ScreenEvents.Remove> removeEvent;
	@Unique
	private Event<ScreenEvents.BeforeTick> beforeTickEvent;
	@Unique
	private Event<ScreenEvents.AfterTick> afterTickEvent;
	@Unique
	private Event<ScreenEvents.BeforeRender> beforeRenderEvent;
	@Unique
	private Event<ScreenEvents.AfterRender> afterRenderEvent;

	// Keyboard
	@Unique
	private Event<ScreenKeyboardEvents.BeforeKeyPressed> beforeKeyPressedEvent;
	@Unique
	private Event<ScreenKeyboardEvents.AfterKeyPressed> afterKeyPressedEvent;
	@Unique
	private Event<ScreenKeyboardEvents.BeforeKeyReleased> beforeKeyReleasedEvent;
	@Unique
	private Event<ScreenKeyboardEvents.AfterKeyReleased> afterKeyReleasedEvent;

	// Mouse
	@Unique
	private Event<ScreenMouseEvents.BeforeMouseClicked> beforeMouseClickedEvent;
	@Unique
	private Event<ScreenMouseEvents.AfterMouseClicked> afterMouseClickedEvent;
	@Unique
	private Event<ScreenMouseEvents.BeforeMouseReleased> beforeMouseReleasedEvent;
	@Unique
	private Event<ScreenMouseEvents.AfterMouseReleased> afterMouseReleasedEvent;
	@Unique
	private Event<ScreenMouseEvents.BeforeMouseScrolled> beforeMouseScrolledEvent;
	@Unique
	private Event<ScreenMouseEvents.AfterMouseScrolled> afterMouseScrolledEvent;

	@Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At("HEAD"))
	private void beforeInitScreen(MinecraftClient client, int width, int height, CallbackInfo ci) {
		// All elements are repopulated on the screen, so we need to reinitialize all events
		this.fabricButtons = null;
		this.removeEvent = ScreenEventFactory.createRemoveEvent();
		this.beforeRenderEvent = ScreenEventFactory.createBeforeRenderEvent();
		this.afterRenderEvent = ScreenEventFactory.createAfterRenderEvent();
		this.beforeTickEvent = ScreenEventFactory.createBeforeTickEvent();
		this.afterTickEvent = ScreenEventFactory.createAfterTickEvent();

		// Keyboard
		this.beforeKeyPressedEvent = ScreenEventFactory.createBeforeKeyPressedEvent();
		this.afterKeyPressedEvent = ScreenEventFactory.createAfterKeyPressedEvent();
		this.beforeKeyReleasedEvent = ScreenEventFactory.createBeforeKeyReleasedEvent();
		this.afterKeyReleasedEvent = ScreenEventFactory.createAfterKeyReleasedEvent();

		// Mouse
		this.beforeMouseClickedEvent = ScreenEventFactory.createBeforeMouseClickedEvent();
		this.afterMouseClickedEvent = ScreenEventFactory.createAfterMouseClickedEvent();
		this.beforeMouseReleasedEvent = ScreenEventFactory.createBeforeMouseReleasedEvent();
		this.afterMouseReleasedEvent = ScreenEventFactory.createAfterMouseReleasedEvent();
		this.beforeMouseScrolledEvent = ScreenEventFactory.createBeforeMouseScrolledEvent();
		this.afterMouseScrolledEvent = ScreenEventFactory.createAfterMouseScrolledEvent();

		ScreenEvents.BEFORE_INIT.invoker().beforeInit(client, (Screen) (Object) this, width, height);
	}

	@Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At("TAIL"))
	private void afterInitScreen(MinecraftClient client, int width, int height, CallbackInfo ci) {
		ScreenEvents.AFTER_INIT.invoker().afterInit(client, (Screen) (Object) this, width, height);
	}

	@Override
	public List<AbstractButtonWidget> fabric_getButtons() {
		// Lazy init to make the list access safe after Screen#init
		if (this.fabricButtons == null) {
			this.fabricButtons = new ButtonList<>(this, this.buttons, this.children);
		}

		return this.fabricButtons;
	}

	@Override
	public Event<ScreenEvents.Remove> fabric_getRemoveEvent() {
		return this.removeEvent;
	}

	@Override
	public Event<ScreenEvents.BeforeTick> fabric_getBeforeTickEvent() {
		return this.beforeTickEvent;
	}

	@Override
	public Event<ScreenEvents.AfterTick> fabric_getAfterTickEvent() {
		return this.afterTickEvent;
	}

	@Override
	public Event<ScreenEvents.BeforeRender> fabric_getBeforeRenderEvent() {
		return this.beforeRenderEvent;
	}

	@Override
	public Event<ScreenEvents.AfterRender> fabric_getAfterRenderEvent() {
		return this.afterRenderEvent;
	}

	// Keyboard

	@Override
	public Event<ScreenKeyboardEvents.BeforeKeyPressed> fabric_getBeforeKeyPressedEvent() {
		return this.beforeKeyPressedEvent;
	}

	@Override
	public Event<ScreenKeyboardEvents.AfterKeyPressed> fabric_getAfterKeyPressedEvent() {
		return this.afterKeyPressedEvent;
	}

	@Override
	public Event<ScreenKeyboardEvents.BeforeKeyReleased> fabric_getBeforeKeyReleasedEvent() {
		return this.beforeKeyReleasedEvent;
	}

	@Override
	public Event<ScreenKeyboardEvents.AfterKeyReleased> fabric_getAfterKeyReleasedEvent() {
		return this.afterKeyReleasedEvent;
	}

	// Mouse

	@Override
	public Event<ScreenMouseEvents.BeforeMouseClicked> fabric_getBeforeMouseClickedEvent() {
		return this.beforeMouseClickedEvent;
	}

	@Override
	public Event<ScreenMouseEvents.AfterMouseClicked> fabric_getAfterMouseClickedEvent() {
		return this.afterMouseClickedEvent;
	}

	@Override
	public Event<ScreenMouseEvents.BeforeMouseReleased> fabric_getBeforeMouseReleasedEvent() {
		return this.beforeMouseReleasedEvent;
	}

	@Override
	public Event<ScreenMouseEvents.AfterMouseReleased> fabric_getAfterMouseReleasedEvent() {
		return this.afterMouseReleasedEvent;
	}

	@Override
	public Event<ScreenMouseEvents.BeforeMouseScrolled> fabric_getBeforeMouseScrolledEvent() {
		return this.beforeMouseScrolledEvent;
	}

	@Override
	public Event<ScreenMouseEvents.AfterMouseScrolled> fabric_getAfterMouseScrolledEvent() {
		return this.afterMouseScrolledEvent;
	}
}
