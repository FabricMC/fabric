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
import net.minecraft.util.profiler.Profiler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

@Environment(EnvType.CLIENT)
public final class ScreenEvents {
	/**
	 * An event that is called after a {@link Screen#init(MinecraftClient, int, int) screen is initialized} to it's default state.
	 *
	 * <p>Typically this event is used to modify a screen after the screen has been initialized.
	 * Modifications such as changing sizes of buttons, removing buttons and adding/removing child elements to the screen can be done safely using this callback.
	 *
	 * <p>For example, to add a button to the title screen, the following code could be used:
	 * <blockquote><pre>
	 * ScreenEvents.AFTER_INIT.register((client, screen, context, scaledWidth, scaledHeight) -> {
	 * 	if (screen instanceof TitleScreen) {
	 * 		context.getButtons().add(new ButtonWidget(...));
	 * 	}
	 * });
	 * </pre></blockquote>
	 *
	 * <p>This event also indicates that the previous screen has been closed.
	 * Mods may also use this callback to clear their current screen state
	 */
	public static final Event<AfterInit> AFTER_INIT = EventFactory.createArrayBacked(AfterInit.class, callbacks -> (client, screen, context, scaledWidth, scaledHeight) -> {
		for (AfterInit callback : callbacks) {
			callback.onInit(client, screen, context, scaledWidth, scaledHeight);
		}
	});

	/**
	 * An event that is called before a screen is rendered.
	 */
	public static final Event<BeforeRender> BEFORE_RENDER = EventFactory.createArrayBacked(BeforeRender.class, callbacks -> (client, matrices, screen, context, mouseX, mouseY, tickDelta) -> {
		if (EventFactory.isProfilingEnabled()) {
			final Profiler profiler = client.getProfiler();
			profiler.push("beforeFabricRenderScreen");

			for (BeforeRender callback : callbacks) {
				profiler.push(EventFactory.getHandlerName(callback));
				callback.beforeRender(client, matrices, screen, context, mouseX, mouseY, tickDelta);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (BeforeRender callback : callbacks) {
				callback.beforeRender(client, matrices, screen, context, mouseX, mouseY, tickDelta);
			}
		}
	});

	/**
	 * An event that is called after a screen is rendered.
	 */
	public static final Event<AfterRender> AFTER_RENDER = EventFactory.createArrayBacked(AfterRender.class, callbacks -> (client, matrices, screen, context, mouseX, mouseY, tickDelta) -> {
		if (EventFactory.isProfilingEnabled()) {
			final Profiler profiler = client.getProfiler();
			profiler.push("afterFabricRenderScreen");

			for (AfterRender callback : callbacks) {
				profiler.push(EventFactory.getHandlerName(callback));
				callback.afterRender(client, matrices, screen, context, mouseX, mouseY, tickDelta);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (AfterRender callback : callbacks) {
				callback.afterRender(client, matrices, screen, context, mouseX, mouseY, tickDelta);
			}
		}
	});

	/**
	 * An event that is called before a screen is ticked.
	 */
	public static final Event<BeforeTick> BEFORE_TICK = EventFactory.createArrayBacked(BeforeTick.class, callbacks -> (client, screen, context) -> {
		if (EventFactory.isProfilingEnabled()) {
			final Profiler profiler = client.getProfiler();
			profiler.push("beforeFabricScreenTick");

			for (BeforeTick callback : callbacks) {
				profiler.push(EventFactory.getHandlerName(callback));
				callback.beforeTick(client, screen, context);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (BeforeTick callback : callbacks) {
				callback.beforeTick(client, screen, context);
			}
		}
	});

	/**
	 * An event that is called after a screen is ticked.
	 */
	public static final Event<AfterTick> AFTER_TICK = EventFactory.createArrayBacked(AfterTick.class, callbacks -> (client, screen, context) -> {
		if (EventFactory.isProfilingEnabled()) {
			final Profiler profiler = client.getProfiler();
			profiler.push("afterFabricScreenTick");

			for (AfterTick callback : callbacks) {
				profiler.push(EventFactory.getHandlerName(callback));
				callback.afterTick(client, screen, context);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (AfterTick callback : callbacks) {
				callback.afterTick(client, screen, context);
			}
		}
	});

	@FunctionalInterface
	public interface AfterInit {
		void onInit(MinecraftClient client, Screen screen, FabricScreen context, int scaledWidth, int scaledHeight);
	}

	@FunctionalInterface
	public interface BeforeRender {
		void beforeRender(MinecraftClient client, MatrixStack matrices, Screen screen, FabricScreen context, int mouseX, int mouseY, float tickDelta);
	}

	@FunctionalInterface
	public interface AfterRender {
		void afterRender(MinecraftClient client, MatrixStack matrices, Screen screen, FabricScreen context, int mouseX, int mouseY, float tickDelta);
	}

	@FunctionalInterface
	public interface BeforeTick {
		void beforeTick(MinecraftClient client, Screen screen, FabricScreen context);
	}

	@FunctionalInterface
	public interface AfterTick {
		void afterTick(MinecraftClient client, Screen screen, FabricScreen context);
	}

	private ScreenEvents() {
	}
}
