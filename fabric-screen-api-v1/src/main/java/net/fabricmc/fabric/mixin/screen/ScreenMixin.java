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
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.text.Text;

import net.fabricmc.fabric.api.client.screen.v1.FabricScreen;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.impl.client.screen.ButtonList;
import net.fabricmc.fabric.impl.client.screen.ScreenEventFactory;

@Mixin(Screen.class)
public abstract class ScreenMixin implements FabricScreen {
	@Shadow
	protected ItemRenderer itemRenderer;
	@Shadow
	protected TextRenderer textRenderer;
	@Shadow
	@Final
	protected List<AbstractButtonWidget> buttons;
	@Shadow
	@Final
	protected List<Element> children;

	@Unique
	private ButtonList<AbstractButtonWidget> fabricButtons;
	@Unique
	private Event<ScreenEvents.BeforeTick> beforeTickEvent;
	@Unique
	private Event<ScreenEvents.AfterTick> afterTickEvent;
	@Unique
	private Event<ScreenEvents.BeforeRender> beforeRenderEvent;
	@Unique
	private Event<ScreenEvents.AfterRender> afterRenderEvent;
	@Unique
	private Event<ScreenEvents.AfterResize> afterResizeEvent;
	@Unique
	private Event<ScreenEvents.BeforeKeyPressed> beforeKeyPressedEvent;
	@Unique
	private Event<ScreenEvents.AfterKeyPressed> afterKeyPressedEvent;
	@Unique
	private Event<ScreenEvents.BeforeKeyReleased> beforeKeyReleasedEvent;
	@Unique
	private Event<ScreenEvents.AfterKeyReleased> afterKeyReleasedEvent;
	@Unique
	private Event<ScreenEvents.BeforeMouseClicked> beforeMouseClickedEvent;
	@Unique
	private Event<ScreenEvents.AfterMouseClicked> afterMouseClickedEvent;
	@Unique
	private Event<ScreenEvents.BeforeMouseReleased> beforeMouseReleasedEvent;
	@Unique
	private Event<ScreenEvents.AfterMouseReleased> afterMouseReleasedEvent;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void initializeEvents(Text title, CallbackInfo ci) {
		this.beforeRenderEvent = ScreenEventFactory.createBeforeRenderEvent();
		this.afterRenderEvent = ScreenEventFactory.createAfterRenderEvent();
		this.beforeTickEvent = ScreenEventFactory.createBeforeTickEvent();
		this.afterTickEvent = ScreenEventFactory.createAfterTickEvent();
		this.afterResizeEvent = ScreenEventFactory.createAfterResizeEvent();
		this.beforeKeyPressedEvent = ScreenEventFactory.createBeforeKeyPressedEvent();
		this.afterKeyPressedEvent = ScreenEventFactory.createAfterKeyPressedEvent();
		this.beforeKeyReleasedEvent = ScreenEventFactory.createBeforeKeyReleasedEvent();
		this.afterKeyReleasedEvent = ScreenEventFactory.createAfterKeyReleasedEvent();
		this.beforeMouseClickedEvent = ScreenEventFactory.createBeforeMouseClickedEvent();
		this.afterMouseClickedEvent = ScreenEventFactory.createAfterMouseClickedEvent();
		this.beforeMouseReleasedEvent = ScreenEventFactory.createBeforeMouseReleasedEvent();
		this.afterMouseReleasedEvent = ScreenEventFactory.createAfterMouseReleasedEvent();
	}

	@Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At("TAIL"))
	private void afterInitScreen(MinecraftClient client, int width, int height, CallbackInfo ci) {
		ScreenEvents.AFTER_INIT.invoker().onInit(client, (Screen) (Object) this, this, width, height);
	}

	@Override
	public List<AbstractButtonWidget> getButtons() {
		// Lazy init to make the list access safe after Screen#init
		if (this.fabricButtons == null) {
			this.fabricButtons = new ButtonList<>(this.buttons, this.children);
		}

		return this.fabricButtons;
	}

	@Override
	public ItemRenderer getItemRenderer() {
		return this.itemRenderer;
	}

	@Override
	public TextRenderer getTextRenderer() {
		return this.textRenderer;
	}

	@Override
	public Event<ScreenEvents.BeforeTick> getBeforeTickEvent() {
		return this.beforeTickEvent;
	}

	@Override
	public Event<ScreenEvents.AfterTick> getAfterTickEvent() {
		return this.afterTickEvent;
	}

	@Override
	public Event<ScreenEvents.BeforeRender> getBeforeRenderEvent() {
		return this.beforeRenderEvent;
	}

	@Override
	public Event<ScreenEvents.AfterRender> getAfterRenderEvent() {
		return this.afterRenderEvent;
	}

	@Override
	public Event<ScreenEvents.AfterResize> getAfterResizeEvent() {
		return this.afterResizeEvent;
	}

	@Override
	public Event<ScreenEvents.BeforeKeyPressed> getBeforeKeyPressedEvent() {
		return this.beforeKeyPressedEvent;
	}

	@Override
	public Event<ScreenEvents.AfterKeyPressed> getAfterKeyPressedEvent() {
		return this.afterKeyPressedEvent;
	}

	@Override
	public Event<ScreenEvents.BeforeKeyReleased> getBeforeKeyReleasedEvent() {
		return this.beforeKeyReleasedEvent;
	}

	@Override
	public Event<ScreenEvents.AfterKeyReleased> getAfterKeyReleasedEvent() {
		return this.afterKeyReleasedEvent;
	}

	@Override
	public Event<ScreenEvents.BeforeMouseClicked> getBeforeMouseClickedEvent() {
		return this.beforeMouseClickedEvent;
	}

	@Override
	public Event<ScreenEvents.AfterMouseClicked> getAfterMouseClickedEvent() {
		return this.afterMouseClickedEvent;
	}

	@Override
	public Event<ScreenEvents.BeforeMouseReleased> getBeforeMouseReleasedEvent() {
		return this.beforeMouseReleasedEvent;
	}

	@Override
	public Event<ScreenEvents.AfterMouseReleased> getAfterMouseReleasedEvent() {
		return this.afterMouseReleasedEvent;
	}

	@Override
	public Screen getScreen() {
		return (Screen) (Object) this;
	}
}
