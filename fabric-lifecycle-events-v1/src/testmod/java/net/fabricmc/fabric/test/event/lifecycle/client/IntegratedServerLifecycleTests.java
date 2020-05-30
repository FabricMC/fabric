package net.fabricmc.fabric.test.event.lifecycle.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class IntegratedServerLifecycleTests implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This should throw an illegal state exception, if it throws anything else, something is wrong. Should not return null since the supplier is setup in the api's ctor.
		try {
			ServerLifecycleEvents.getCurrentServer();
		} catch (IllegalStateException ignored) {
			// Do nothing, this is intended
		}
	}
}
