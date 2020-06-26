/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.api.object.builder.v1.entity;

import java.util.Objects;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.block.Block;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import net.fabricmc.fabric.impl.object.builder.FabricEntityType;
import net.fabricmc.fabric.mixin.object.builder.SpawnRestrictionAccessor;

/**
 * Extended version of {@link EntityType.Builder} with added registration for
 * server-&gt;client entity tracking values.
 *
 * @param <T> Entity class.
 */
public class FabricEntityTypeBuilder<T extends Entity> {
	private static final Logger LOGGER = LogManager.getLogger();
	private final SpawnGroup spawnGroup;
	private final EntityType.EntityFactory<T> function;
	private boolean saveable = true;
	private boolean summonable = true;
	private int trackingDistance = 5;
	private int updateIntervalTicks = 3;
	private Boolean alwaysUpdateVelocity;
	private boolean fireImmune = false;
	private boolean spawnableFarFromPlayer;
	private EntityDimensions dimensions = EntityDimensions.changing(-1.0f, -1.0f);
	private ImmutableSet<Block> specificSpawnBlocks = ImmutableSet.of();

	protected FabricEntityTypeBuilder(SpawnGroup spawnGroup, EntityType.EntityFactory<T> function) {
		this.spawnGroup = spawnGroup;
		this.function = function;
		this.spawnableFarFromPlayer = spawnGroup == SpawnGroup.CREATURE || spawnGroup == SpawnGroup.MISC;
	}

	/**
	 * Creates an entity type builder.
	 *
	 * <p>This entity's spawn group will automatically be set to {@link SpawnGroup#MISC}.
	 *
	 * @param <T> the type of entity
	 *
	 * @return a new entity type builder
	 */
	public static <T extends Entity> FabricEntityTypeBuilder<T> create() {
		return create(SpawnGroup.MISC);
	}

	/**
	 * Creates an entity type builder.
	 *
	 * <p>This entity's spawn group will automatically be set to {@link SpawnGroup#MISC}.
	 *
	 * @param function the entity function used to create this entity
	 * @param <T> the type of entity
	 *
	 * @return a new entity type builder
	 */
	public static <T extends Entity> FabricEntityTypeBuilder<T> create(EntityType.EntityFactory<T> function) {
		return create(SpawnGroup.MISC, function);
	}

	/**
	 * Creates an entity type builder.
	 *
	 * @param spawnGroup the entity spawn group
	 * @param <T> the type of entity
	 *
	 * @return a new entity type builder
	 */
	public static <T extends Entity> FabricEntityTypeBuilder<T> create(SpawnGroup spawnGroup) {
		return create(spawnGroup, FabricEntityTypeBuilder::empty);
	}

	/**
	 * Creates an entity type builder.
	 *
	 * @param spawnGroup the entity spawn group
	 * @param function the entity function used to create this entity
	 * @param <T> the type of entity
	 *
	 * @return a new entity type builder
	 */
	public static <T extends Entity> FabricEntityTypeBuilder<T> create(SpawnGroup spawnGroup, EntityType.EntityFactory<T> function) {
		return new FabricEntityTypeBuilder<>(spawnGroup, function);
	}

	/**
	 * Creates an entity type builder for a living entity.
	 *
	 * @param spawnGroup the entity spawn group
	 * @param factory the entity factory used to create this entity
	 * @param <T> the type of entity
	 *
	 * @return a new living entity type builder
	 */
	public static <T extends LivingEntity> FabricEntityTypeBuilder.Living<T> createLiving(SpawnGroup spawnGroup, EntityType.EntityFactory<T> factory) {
		return new FabricEntityTypeBuilder.Living<>(spawnGroup, factory);
	}

	/**
	 * Creates an entity type builder for a living entity.
	 *
	 * @param spawnGroup the entity spawn group
	 * @param <T> the type of entity
	 *
	 * @return a new living entity type builder
	 */
	public static <T extends LivingEntity> FabricEntityTypeBuilder.Living<T> createLiving(SpawnGroup spawnGroup) {
		return createLiving(spawnGroup, FabricEntityTypeBuilder::empty);
	}

	/**
	 * Creates an entity type builder for a living entity.
	 *
	 * <p>This entity's spawn group will automatically be set to {@link SpawnGroup#MISC}.
	 *
	 * @param factory the entity factory used to create this entity
	 * @param <T> the type of entity
	 *
	 * @return a new living entity type builder
	 */
	public static <T extends LivingEntity> FabricEntityTypeBuilder.Living<T> createLiving(EntityType.EntityFactory<T> factory) {
		return createLiving(SpawnGroup.MISC, factory);
	}

	/**
	 * Creates an entity type builder for a living entity.
	 *
	 * <p>This entity's spawn group will automatically be set to {@link SpawnGroup#MISC}.
	 *
	 * @param <T> the type of entity
	 *
	 * @return a new living entity type builder
	 */
	public static <T extends LivingEntity> FabricEntityTypeBuilder.Living<T> createLiving() {
		return createLiving(SpawnGroup.MISC, FabricEntityTypeBuilder::empty);
	}

	/**
	 * Creates an entity type builder for a mob entity.
	 *
	 * @param spawnGroup the entity spawn group
	 * @param factory the entity factory used to create this entity
	 * @param <T> the type of entity
	 *
	 * @return a new mob entity type builder
	 */
	public static <T extends MobEntity> FabricEntityTypeBuilder.Mob<T> createMob(SpawnGroup spawnGroup, EntityType.EntityFactory<T> factory) {
		return new FabricEntityTypeBuilder.Mob<>(spawnGroup, factory);
	}

	/**
	 * Creates an entity type builder for a mob entity.
	 *
	 * @param spawnGroup the entity spawn group
	 * @param <T> the type of entity
	 *
	 * @return a new mob entity type builder
	 */
	public static <T extends MobEntity> FabricEntityTypeBuilder.Mob<T> createMob(SpawnGroup spawnGroup) {
		return new FabricEntityTypeBuilder.Mob<>(spawnGroup, FabricEntityTypeBuilder::empty);
	}

	private static <T extends Entity> T empty(EntityType<T> type, World world) {
		return null;
	}

	/**
	 * Whether this entity type is summonable using the {@code /summon} command.
	 *
	 * @return this builder for chaining
	 */
	public FabricEntityTypeBuilder<T> disableSummon() {
		this.summonable = false;
		return this;
	}

	public FabricEntityTypeBuilder<T> disableSaving() {
		this.saveable = false;
		return this;
	}

	/**
	 * Sets this entity type to be fire immune.
	 *
	 * @return this builder for chaining
	 */
	public FabricEntityTypeBuilder<T> fireImmune() {
		this.fireImmune = true;
		return this;
	}

	/**
	 * Sets whether this entity type can be spawned far away from a player.
	 *
	 * @return this builder for chaining
	 */
	public FabricEntityTypeBuilder<T> spawnableFarFromPlayer() {
		this.spawnableFarFromPlayer = true;
		return this;
	}

	/**
	 * Sets the dimensions of this entity type.
	 *
	 * @param dimensions the dimensions representing the entity's size
	 *
	 * @return this builder for chaining
	 */
	public FabricEntityTypeBuilder<T> dimensions(EntityDimensions dimensions) {
		Objects.requireNonNull(dimensions, "Cannot set null dimensions");
		this.dimensions = dimensions;
		return this;
	}

	public FabricEntityTypeBuilder<T> trackable(int trackingDistanceBlocks, int updateIntervalTicks) {
		return trackable(trackingDistanceBlocks, updateIntervalTicks, true);
	}

	public FabricEntityTypeBuilder<T> trackable(int trackingDistanceBlocks, int updateIntervalTicks, boolean alwaysUpdateVelocity) {
		this.trackingDistance = trackingDistanceBlocks;
		this.updateIntervalTicks = updateIntervalTicks;
		this.alwaysUpdateVelocity = alwaysUpdateVelocity;
		return this;
	}

	/**
	 * Sets the {@link ImmutableSet} of blocks this entity can spawn on.
	 *
	 * @param blocks the blocks the entity can spawn on
	 * @return this builder for chaining
	 */
	public FabricEntityTypeBuilder<T> specificSpawnBlocks(Block... blocks) {
		this.specificSpawnBlocks = ImmutableSet.copyOf(blocks);
		return this;
	}

	/**
	 * Creates the entity type.
	 *
	 * @return a new {@link EntityType}
	 */
	public EntityType<T> build() {
		if (this.saveable) {
			// SNIP! Modded datafixers are not supported anyway.
			// TODO: Flesh out once modded datafixers exist.
		}

		EntityType<T> type = new FabricEntityType<>(this.function, this.spawnGroup, this.saveable, this.summonable, this.fireImmune, this.spawnableFarFromPlayer, this.specificSpawnBlocks, dimensions, trackingDistance, updateIntervalTicks, alwaysUpdateVelocity);

		return type;
	}

	public static class Living<T extends LivingEntity> extends FabricEntityTypeBuilder<T> {
		/* @Nullable */
		private Supplier<DefaultAttributeContainer.Builder> defaultAttributeBuilder;

		protected Living(SpawnGroup spawnGroup, EntityType.EntityFactory<T> function) {
			super(spawnGroup, function);
		}

		@Override
		public FabricEntityTypeBuilder.Living<T> disableSummon() {
			super.disableSummon();
			return this;
		}

		@Override
		public FabricEntityTypeBuilder.Living<T> disableSaving() {
			super.disableSaving();
			return this;
		}

		@Override
		public FabricEntityTypeBuilder.Living<T> fireImmune() {
			super.fireImmune();
			return this;
		}

		@Override
		public FabricEntityTypeBuilder.Living<T> spawnableFarFromPlayer() {
			super.spawnableFarFromPlayer();
			return this;
		}

		@Override
		public FabricEntityTypeBuilder.Living<T> dimensions(EntityDimensions dimensions) {
			super.dimensions(dimensions);
			return this;
		}

		@Override
		public FabricEntityTypeBuilder.Living<T> trackable(int trackingDistanceBlocks, int updateIntervalTicks) {
			super.trackable(trackingDistanceBlocks, updateIntervalTicks);
			return this;
		}

		@Override
		public FabricEntityTypeBuilder.Living<T> trackable(int trackingDistanceBlocks, int updateIntervalTicks, boolean alwaysUpdateVelocity) {
			super.trackable(trackingDistanceBlocks, updateIntervalTicks, alwaysUpdateVelocity);
			return this;
		}

		@Override
		public FabricEntityTypeBuilder.Living<T> specificSpawnBlocks(Block... blocks) {
			super.specificSpawnBlocks(blocks);
			return this;
		}

		/**
		 * Sets the default attributes for a type of living entity.
		 *
		 * <p>This can be used in a fashion similar to this:
		 * <blockquote><pre>
		 * FabricEntityTypeBuilder.createLiving(SpawnGroup.CREATURE, MyCreature::new)
		 * 	.defaultAttributes(LivingEntity::createLivingAttributes)
		 * 	...
		 * 	.build();
		 * </pre></blockquote>
		 *
		 * @param defaultAttributeBuilder a function to generate the default attribute builder from the entity type
		 * @return this builder for chaining
		 */
		public FabricEntityTypeBuilder.Living<T> defaultAttributes(Supplier<DefaultAttributeContainer.Builder> defaultAttributeBuilder) {
			Objects.requireNonNull(defaultAttributeBuilder, "Cannot set null attribute builder");
			this.defaultAttributeBuilder = defaultAttributeBuilder;
			return this;
		}

		@Override
		public EntityType<T> build() {
			final EntityType<T> type = super.build();

			if (this.defaultAttributeBuilder != null) {
				FabricDefaultAttributeRegistry.register(type, this.defaultAttributeBuilder.get());
			}

			return type;
		}
	}

	public static class Mob<T extends MobEntity> extends FabricEntityTypeBuilder.Living<T> {
		private SpawnRestriction.Location restrictionLocation;
		private Heightmap.Type restrictionHeightmap;
		private SpawnRestriction.SpawnPredicate<T> spawnPredicate;

		protected Mob(SpawnGroup spawnGroup, EntityType.EntityFactory<T> function) {
			super(spawnGroup, function);
		}

		@Override
		public FabricEntityTypeBuilder.Mob<T> disableSummon() {
			super.disableSummon();
			return this;
		}

		@Override
		public FabricEntityTypeBuilder.Mob<T> disableSaving() {
			super.disableSaving();
			return this;
		}

		@Override
		public FabricEntityTypeBuilder.Mob<T> fireImmune() {
			super.fireImmune();
			return this;
		}

		@Override
		public FabricEntityTypeBuilder.Mob<T> spawnableFarFromPlayer() {
			super.spawnableFarFromPlayer();
			return this;
		}

		@Override
		public FabricEntityTypeBuilder.Mob<T> dimensions(EntityDimensions dimensions) {
			super.dimensions(dimensions);
			return this;
		}

		@Override
		public FabricEntityTypeBuilder.Mob<T> trackable(int trackingDistanceBlocks, int updateIntervalTicks) {
			super.trackable(trackingDistanceBlocks, updateIntervalTicks);
			return this;
		}

		@Override
		public FabricEntityTypeBuilder.Mob<T> trackable(int trackingDistanceBlocks, int updateIntervalTicks, boolean alwaysUpdateVelocity) {
			super.trackable(trackingDistanceBlocks, updateIntervalTicks, alwaysUpdateVelocity);
			return this;
		}

		@Override
		public FabricEntityTypeBuilder.Mob<T> specificSpawnBlocks(Block... blocks) {
			super.specificSpawnBlocks(blocks);
			return this;
		}

		@Override
		public FabricEntityTypeBuilder.Mob<T> defaultAttributes(Supplier<DefaultAttributeContainer.Builder> defaultAttributeBuilder) {
			super.defaultAttributes(defaultAttributeBuilder);
			return this;
		}

		/**
		 * Registers a spawn restriction for this entity.
		 *
		 * <p>This is used by mobs to determine whether Minecraft should spawn an entity within a certain context.
		 *
		 * @return this builder for chaining.
		 */
		public FabricEntityTypeBuilder.Mob<T> spawnRestriction(SpawnRestriction.Location location, Heightmap.Type heightmap, SpawnRestriction.SpawnPredicate<T> spawnPredicate) {
			this.restrictionLocation = Objects.requireNonNull(location, "Location cannot be null.");
			this.restrictionHeightmap = Objects.requireNonNull(heightmap, "Heightmap type cannot be null.");
			this.spawnPredicate = Objects.requireNonNull(spawnPredicate, "Spawn predicate cannot be null.");
			return this;
		}

		@Override
		public EntityType<T> build() {
			EntityType<T> type = super.build();

			if (this.spawnPredicate != null) {
				SpawnRestrictionAccessor.callRegister(type, this.restrictionLocation, this.restrictionHeightmap, this.spawnPredicate);
			}

			return type;
		}
	}
}
