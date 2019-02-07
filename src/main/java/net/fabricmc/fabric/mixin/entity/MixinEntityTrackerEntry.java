package net.fabricmc.fabric.mixin.entity;

import net.fabricmc.fabric.impl.server.EntityTrackerEntryStreamAccessor;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;
import java.util.stream.Stream;

@Mixin(EntityTrackerEntry.class)
public class MixinEntityTrackerEntry implements EntityTrackerEntryStreamAccessor {
	@Shadow
	private Set<ServerPlayerEntity> trackingPlayers;

	@Override
	public Stream<ServerPlayerEntity> fabric_getTrackingPlayers() {
		return trackingPlayers.stream();
	}
}
