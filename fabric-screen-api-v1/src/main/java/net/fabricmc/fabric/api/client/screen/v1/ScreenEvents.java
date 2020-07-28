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

package net.fabricmc.fabric.api.client.screen.v1;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

// TODO:
// Char typed
// Add Child
// Add button
// Change ButtonList to fire add child and button events
@Environment(EnvType.CLIENT)
public final class ScreenEvents {
	/**
	 * An event that is called before a {@link Screen#init(MinecraftClient, int, int) screen is initialized} to it's default state.
	 * It should be noted many of the methods in {@link FabricScreen} such as the screen's text renderer may not be initialized yet, and as such their use is discouraged.
	 *
	 * <p>Typically this event is used to register screen events such as listening to when child elements are added to the screen.
	 * You can still use {@link ScreenEvents#AFTER_INIT} to register events such as keyboard and mouse events.
	 *
	 * <p>The {@link FabricScreen} provided by the {@code info} parameter may be used to register tick, render events, keyboard, mouse, additional and removal of child elements (including buttons).
	 * For example, to register an event on inventory like screens after render, the following code could be used:
	 * <blockquote><pre>
	 * &#64;Override
	 * public void onInitializeClient() {
	 * 	ScreenEvents.AFTER_INIT.register((client, screen, info, scaledWidth, scaledHeight) -> {
	 * 		if (screen instanceof AbstractInventoryScreen) {
	 * 			info.getAfterRenderEvent().register(this::onRenderInventoryScreen);
	 * 		}
	 * 	});
	 * }
	 *
	 * private void onRenderInventoryScreen(MinecraftClient client, MatrixStack matrices, Screen screen, FabricScreen info, int mouseX, int mouseY, float tickDelta) {
	 * 	...
	 * }
	 * </pre></blockquote>
	 *
	 * <p>This event indicates a screen has been resized, and therefore is being re-initialized.
	 * This event can also indicate that the previous screen has been closed.
	 * @see ScreenEvents#AFTER_INIT
	 */
	public static final Event<ScreenEvents.BeforeInit> BEFORE_INIT = EventFactory.createArrayBacked(ScreenEvents.BeforeInit.class, callbacks -> (client, screen, info, scaledWidth, scaledHeight) -> {
		for (BeforeInit callback : callbacks) {
			callback.beforeInit(client, screen, info, scaledWidth, scaledHeight);
		}
	});

	/**
	 * An event that is called after a {@link Screen#init(MinecraftClient, int, int) screen is initialized} to it's default state.
	 * Since this event is fired after a screen has been initialized,
	 *
	 * <p>Typically this event is used to modify a screen after the screen has been initialized.
	 * Modifications such as changing sizes of buttons, removing buttons and adding/removing child elements to the screen can be done safely using this callback.
	 *
	 * <p>For example, to add a button to the title screen, the following code could be used:
	 * <blockquote><pre>
	 * ScreenEvents.AFTER_INIT.register((client, screen, info, scaledWidth, scaledHeight) -> {
	 * 	if (screen instanceof TitleScreen) {
	 * 		context.getButtons().add(new ButtonWidget(...));
	 * 	}
	 * });
	 * </pre></blockquote>
	 *
	 * <p>This event can also indicate that the previous screen has been closed.
	 * @see ScreenEvents#BEFORE_INIT
	 */
	public static final Event<AfterInit> AFTER_INIT = EventFactory.createArrayBacked(AfterInit.class, callbacks -> (client, screen, info, scaledWidth, scaledHeight) -> {
		for (AfterInit callback : callbacks) {
			callback.afterInit(client, screen, info, scaledWidth, scaledHeight);
		}
	});

	@FunctionalInterface
	public interface BeforeInit {
		void beforeInit(MinecraftClient client, Screen screen, FabricScreen info, int scaledWidth, int scaledHeight);
	}

	@FunctionalInterface
	public interface AfterInit {
		void afterInit(MinecraftClient client, Screen screen, FabricScreen info, int scaledWidth, int scaledHeight);
	}

	@FunctionalInterface
	public interface BeforeRender {
		void beforeRender(MinecraftClient client, MatrixStack matrices, Screen screen, FabricScreen info, int mouseX, int mouseY, float tickDelta);
	}

	@FunctionalInterface
	public interface AfterRender {
		void afterRender(MinecraftClient client, MatrixStack matrices, Screen screen, FabricScreen info, int mouseX, int mouseY, float tickDelta);
	}

	@FunctionalInterface
	public interface BeforeTick {
		void beforeTick(MinecraftClient client, Screen screen, FabricScreen info);
	}

	@FunctionalInterface
	public interface AfterTick {
		void afterTick(MinecraftClient client, Screen screen, FabricScreen info);
	}

	@FunctionalInterface
	public interface BeforeKeyPressed {
		boolean beforeKeyPress(MinecraftClient client, Screen screen, FabricScreen info, int key, int scancode, int modifiers);
	}

	@FunctionalInterface
	public interface AfterKeyPressed {
		void afterKeyPress(MinecraftClient client, Screen screen, FabricScreen info, int key, int scancode, int modifiers);
	}

	@FunctionalInterface
	public interface BeforeKeyReleased {
		boolean beforeKeyReleased(MinecraftClient client, Screen screen, FabricScreen info, int key, int scancode, int modifiers);
	}

	@FunctionalInterface
	public interface AfterKeyReleased {
		void afterKeyReleased(MinecraftClient client, Screen screen, FabricScreen info, int key, int scancode, int modifiers);
	}

	@FunctionalInterface
	public interface BeforeMouseClicked {
		boolean beforeMouseClicked(MinecraftClient client, Screen screen, FabricScreen info, double mouseX, double mouseY, int button);
	}

	@FunctionalInterface
	public interface AfterMouseClicked {
		void afterMouseClicked(MinecraftClient client, Screen screen, FabricScreen info, double mouseX, double mouseY, int button);
	}

	@FunctionalInterface
	public interface BeforeMouseReleased {
		boolean beforeMouseReleased(MinecraftClient client, Screen screen, FabricScreen info, double mouseX, double mouseY, int button);
	}

	@FunctionalInterface
	public interface AfterMouseReleased {
		void afterMouseReleased(MinecraftClient client, Screen screen, FabricScreen info, double mouseX, double mouseY, int button);
	}

	@FunctionalInterface
	public interface BeforeMouseScrolled {
		boolean beforeMouseScrolled(MinecraftClient client, Screen screen, FabricScreen info, double mouseX, double mouseY, double horizontalAmount, double verticalAmount);
	}

	@FunctionalInterface
	public interface AfterMouseScrolled {
		void afterMouseScrolled(MinecraftClient client, Screen screen, FabricScreen info, double mouseX, double mouseY, double horizontalAmount, double verticalAmount);
	}

	private ScreenEvents() {
	}
}
