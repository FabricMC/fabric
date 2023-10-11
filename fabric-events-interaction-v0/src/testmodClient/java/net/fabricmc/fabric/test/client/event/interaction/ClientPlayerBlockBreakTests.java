package net.fabricmc.fabric.test.client.event.interaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents;

public class ClientPlayerBlockBreakTests implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger(ClientPlayerBlockBreakTests.class);

	@Override
	public void onInitializeClient() {
		ClientPlayerBlockBreakEvents.AFTER.register(((world, player, pos, state, entity) -> LOGGER.info("Block broken at {}, {}, {} (client-side = {})", pos.getX(), pos.getY(), pos.getZ(), world.isClient())));
	}
}
