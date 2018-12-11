package net.fabricmc.fabric.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.Packet;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

// TODO: javadocs
public class EntityTrackingRegistry {
	public static class Entry {
		private final int trackingDistance;
		private final int updateIntervalTicks;
		private final boolean alwaysUpdateVelocity;

		public Entry(int trackingDistance, int updateIntervalTicks, boolean alwaysUpdateVelocity) {
			this.trackingDistance = trackingDistance;
			this.updateIntervalTicks = updateIntervalTicks;
			this.alwaysUpdateVelocity = alwaysUpdateVelocity;
		}

		public int getTrackingDistance() {
			return trackingDistance;
		}

		public int getUpdateIntervalTicks() {
			return updateIntervalTicks;
		}

		public boolean alwaysUpdateVelocity() {
			return alwaysUpdateVelocity;
		}
	}

	public static final EntityTrackingRegistry INSTANCE = new EntityTrackingRegistry();
	private final Map<EntityType, Entry> entries = new HashMap<>();
	private final Map<EntityType, Function<Entity, Packet>> spawnPacketProviders = new HashMap<>();

	private EntityTrackingRegistry() {

	}

	public Entry get(EntityType type) {
		return entries.get(type);
	}

	public Packet createSpawnPacket(Entity entity) {
		Function<Entity, Packet> packetFunction = spawnPacketProviders.get(entity.getType());
		if (packetFunction != null) {
			return packetFunction.apply(entity);
		} else {
			return null;
		}
	}

	public void register(EntityType type, int trackingDistance, int updateIntervalTicks) {
		register(type, trackingDistance, updateIntervalTicks, true);
	}

	public void register(EntityType type, int trackingDistance, int updateIntervalTicks, boolean alwaysUpdateVelocity) {
		entries.put(type, new Entry(trackingDistance, updateIntervalTicks, alwaysUpdateVelocity));
	}

	public void registerSpawnPacketProvider(EntityType type, Function<Entity, Packet> packetFunction) {
		spawnPacketProviders.put(type, packetFunction);
	}
}
