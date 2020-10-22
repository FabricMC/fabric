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

package net.fabricmc.fabric.api.entity.event.v1;

import java.util.Optional;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;

/**
 * A utilities providing mechanisms to help teleport entities to other server worlds.
 */
public final class EntityTeleportationHelper {
	/**
	 * Teleports an entity to a different world.
	 *
	 * @param destination the world to teleport the entity to
	 * @param entity the entity to teleport
	 * @param x the x position to teleport the entity to
	 * @param y the y position to teleport the entity to
	 * @param z the z position to teleport the entity to
	 * @param yaw the entity's yaw after teleportation
	 * @param pitch the entity's pitch after teleportation
	 *
	 * @return {@link Optional#empty()} if:
	 * <ul>
	 * <li>The entity is {@code null}.</li>
	 * <li>The entity is in a {@link ClientWorld}.</li>
	 * <li>The entity couldn't be created in the new world. Usually because it is {@link EntityType#isSummonable() not summonable}.</li>
	 * </ul>
	 *
	 * The return value will contain an entity in the following cases:
	 * <ul>
	 * <li>The entity is a {@link ServerPlayerEntity}. In this case the return value is the same player.</li>
	 * <li>The entity's {@link Entity#world current world} is the same as the destination world. As a result, the entity will be moved to the new location.</li>
	 * <li>The entity was successfully moved to the new world.</li>
	 * </ul>
	 */
	public static Optional<Entity> teleportToWorld(ServerWorld destination, Entity entity, double x, double y, double z, float yaw, float pitch) {
		// Logic here is the same as TeleportCommand, just without teleport flags or package-private facing location.
		// Server players are moved to the other world and not copied, so fall to builtin logic for player entities.
		if (entity instanceof ServerPlayerEntity) {
			((ServerPlayerEntity) entity).teleport(destination, x, y, z, yaw, pitch);
			return Optional.of(entity);
		}

		// Do not teleport null entities or entities on the wrong logical side
		if (entity == null || entity.world.isClient()) {
			return Optional.empty();
		}

		final float wrappedYaw = MathHelper.wrapDegrees(yaw);
		final float wrappedPitch = MathHelper.clamp(MathHelper.wrapDegrees(pitch), -90.0F, 90.0F);

		// If the entity's world didn't change, just move the entity to the new location
		if (destination == entity.world) {
			entity.refreshPositionAndAngles(x, y, z, wrappedYaw, wrappedPitch);
			entity.setHeadYaw(wrappedYaw);
			return Optional.of(entity);
		}

		final Entity newEntity = entity.getType().create(destination);

		if (newEntity == null) {
			return Optional.empty(); // Entity is not summonable, so return and keep the old entity in the world
		}

		// The entity is guaranteed to be moved to another world
		entity.detach();

		// Copy old entity to this new entity
		newEntity.copyFrom(entity);
		newEntity.refreshPositionAndAngles(x, y, z, yaw, pitch);
		newEntity.setYaw(yaw);

		// So the new entity is added to the world's chunk
		destination.onDimensionChanged(newEntity);

		// Fire the changed world event - Fabric added
		EntityWorldChangeEvents.AFTER_ENTITY_CHANGED_WORLD.invoker().afterChangeWorld(entity, newEntity, (ServerWorld) entity.world, (ServerWorld) newEntity.world);

		entity.removed = true; // Mark the old entity as removed

		// Copy over the entity's velocity, unless it is flying using something like an elytra
		if (!(newEntity instanceof LivingEntity) || !((LivingEntity) newEntity).isFallFlying()) {
			newEntity.setVelocity(entity.getVelocity().multiply(1.0D, 0.0D, 1.0D));
			entity.setOnGround(true);
		}

		// Stop the entity's navigation
		if (newEntity instanceof PathAwareEntity) {
			((PathAwareEntity) newEntity).getNavigation().stop();
		}

		return Optional.of(newEntity);
	}
}
