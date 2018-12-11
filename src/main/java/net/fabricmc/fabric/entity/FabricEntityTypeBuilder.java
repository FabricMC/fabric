package net.fabricmc.fabric.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityType.Builder;
import net.minecraft.world.World;

import java.util.function.Function;

// TODO: javadocs
public class FabricEntityTypeBuilder<T extends Entity> {
	protected final EntityType.Builder<T> delegate;
	private int trackingDistance = -1;
	private int updateIntervalTicks = -1;
	private boolean alwaysUpdateVelocity = true;

	protected FabricEntityTypeBuilder(EntityType.Builder<T> delegate) {
		this.delegate = delegate;
	}

	public static <T extends Entity> FabricEntityTypeBuilder<T> create(Class<? extends T> entityClass) {
		return new FabricEntityTypeBuilder<>(EntityType.Builder.create(entityClass));
	}

	public static <T extends Entity> FabricEntityTypeBuilder<T> create(Class<? extends T> entityClass, Function<? super World, ? extends T> function) {
		return new FabricEntityTypeBuilder<>(EntityType.Builder.create(entityClass, function));
	}

	public FabricEntityTypeBuilder<T> disableSummon() {
		delegate.disableSummon();
		return this;
	}

	public FabricEntityTypeBuilder<T> disableSaving() {
		delegate.disableSaving();
		return this;
	}

	public FabricEntityTypeBuilder<T> trackable(int trackingDistance, int updateIntervalTicks) {
		return trackable(trackingDistance, updateIntervalTicks, true);
	}

	public FabricEntityTypeBuilder<T> trackable(int trackingDistance, int updateIntervalTicks, boolean alwaysUpdateVelocity) {
		this.trackingDistance = trackingDistance;
		this.updateIntervalTicks = updateIntervalTicks;
		this.alwaysUpdateVelocity = alwaysUpdateVelocity;
		return this;
	}

	public EntityType<T> build(String id) {
		EntityType type = delegate.build(id);
		if (trackingDistance != -1) {
			EntityTrackingRegistry.INSTANCE.register(type, trackingDistance, updateIntervalTicks, alwaysUpdateVelocity);
		}
		return type;
	}
}
