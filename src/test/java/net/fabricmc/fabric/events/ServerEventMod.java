package net.fabricmc.fabric.events;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerEventMod implements ModInitializer {
	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void onInitialize() {
		ServerEvent.START.register(server -> LOGGER.info("Server starting (" + server + ")"));
		ServerEvent.STOP.register(server -> LOGGER.info("Server stopping (" + server + ")"));
	}
}
