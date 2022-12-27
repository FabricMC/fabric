package net.fabricmc.fabric.test.event.interaction;

import net.fabricmc.fabric.api.event.player.PlayerPlaceBlockEvents;

import net.minecraft.util.ActionResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;

public class PlayerPlaceBlockTests implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("InteractionEventsTest");

	@Override
	public void onInitialize() {
		PlayerPlaceBlockEvents.BEFORE.register((context -> {
			if(!context.getWorld().isClient) LOGGER.info("Player attempted to place a block at " + context.getBlockPos().getX() + ", " + context.getBlockPos().getY() + ", " + context.getBlockPos().getZ());
			return ActionResult.PASS;
		}));

		PlayerPlaceBlockEvents.AFTER.register((world, player, pos, state) -> {
			if(!world.isClient) LOGGER.info("Player placed a block at " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
		});

		PlayerPlaceBlockEvents.CANCELLED.register(((world, player, pos, state) -> {
			if(!world.isClient) LOGGER.info("Player cancelled a block placement at " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
		}));

		PlayerPlaceBlockEvents.ALLOW.register(entity -> {
			return true;
		});
	}
}
