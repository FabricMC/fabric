package net.fabricmc.fabric.test.event.lifecycle;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class DedicatedServerLifecycleTests implements DedicatedServerModInitializer {
	@Override
	public void onInitializeServer() {
		if (!ServerLifecycleEvents.isServerAvailable()) {
			throw new AssertionError("Server should be available always on a dedicated server");
		}

		// Should throw no exceptions
		ServerLifecycleEvents.getCurrentServer();
	}
}
