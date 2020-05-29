package net.fabricmc.fabric.api.client.screen.v1;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.profiler.Profiler;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface ScreenTickCallback {
	Event<ScreenTickCallback> EVENT = EventFactory.createArrayBacked(ScreenTickCallback.class, callbacks -> (client, screen, context) -> {
		if (EventFactory.isProfilingEnabled()) {
			final Profiler profiler = client.getProfiler();
			profiler.push("fabricScreenTick");

			for (ScreenTickCallback callback : callbacks) {
				profiler.push(EventFactory.getHandlerName(callback));
				callback.onTick(client, screen, context);
				profiler.pop();
			}

			profiler.pop();
		} else {
			for (ScreenTickCallback callback : callbacks) {
				callback.onTick(client, screen, context);
			}
		}
	});

	void onTick(MinecraftClient client, Screen screen, ScreenContext context);
}
