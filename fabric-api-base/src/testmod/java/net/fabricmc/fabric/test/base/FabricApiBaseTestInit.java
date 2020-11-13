package net.fabricmc.fabric.test.base;

import org.spongepowered.asm.mixin.MixinEnvironment;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class FabricApiBaseTestInit implements ModInitializer {
	private int ticks = 0;

	@Override
	public void onInitialize() {
		if (Boolean.parseBoolean(System.getProperty("fabric.autoTest", "false"))) {
			ServerTickEvents.END_SERVER_TICK.register(server -> {
				ticks++;

				if (ticks == 50) {
					MixinEnvironment.getCurrentEnvironment().audit();
					server.stop(false);
				}
			});
		}
	}
}
