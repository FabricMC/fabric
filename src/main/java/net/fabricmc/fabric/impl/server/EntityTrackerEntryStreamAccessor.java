package net.fabricmc.fabric.impl.server;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.stream.Stream;

public interface EntityTrackerEntryStreamAccessor {
	Stream<ServerPlayerEntity> fabric_getTrackingPlayers();
}
