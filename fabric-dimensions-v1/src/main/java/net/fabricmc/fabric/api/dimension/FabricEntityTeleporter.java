/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.api.dimension;

import net.fabricmc.fabric.impl.entity.TeleportingServerPlayerEntity;
import net.minecraft.client.network.packet.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelProperties;

/**
 * Entity teleporter that ignores vanilla placement actions (such as nether portals being built).
 */
public class FabricEntityTeleporter {
    public static void changeDimension(Entity entity, DimensionType destinationDimension, EntityPlacer placementLogic) {
        if(entity.getEntityWorld().isClient) {
            System.out.println("ONLY CALL FROM SERVER");
            return;
        }

        ServerWorld previousWorld = (ServerWorld) entity.getEntityWorld();
        ServerWorld destinationWorld = entity.getServer().getWorld(destinationDimension);

        entity.dimension = destinationDimension;

        if(entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            sendLevelPackets(player, destinationDimension);
            PlayerManager playerManager = player.getServer().getPlayerManager();
            playerManager.sendCommandTree(player);
            previousWorld.removePlayer(player);
        }

        entity.removed = false;

        if(destinationDimension instanceof FabricDimensionType || placementLogic != null) {
            runPlacementLogic(entity, placementLogic, destinationDimension, destinationWorld);
        }

        entity.setWorld(destinationWorld);

        if(entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            destinationWorld.method_18211(player);
            ((TeleportingServerPlayerEntity) entity).handleDimensionCriterions(previousWorld);
            player.networkHandler.requestTeleport(entity.x, entity.y, entity.z, entity.yaw, entity.pitch);
            player.interactionManager.setWorld(destinationWorld);
            player.networkHandler.sendPacket(new PlayerAbilitiesS2CPacket(player.abilities));
            player.getServer().getPlayerManager().sendWorldInfo(player, destinationWorld);
            player.getServer().getPlayerManager().method_14594(player);
			reappplyStatusEffects(player);
			player.networkHandler.sendPacket(new WorldEventS2CPacket(1032, BlockPos.ORIGIN, 0, false));
            refreshTrackerValues(player);
        }

        else {
            destinationWorld.method_18769(entity);
        }
    }


    /**
     * Applies status effects from the old world to the entity in the new world.
	 * Not needed for Entity.
	 * @param player player to apply effects to
	 */
	private static void reappplyStatusEffects(ServerPlayerEntity player) {
		for (StatusEffectInstance statusEffect : player.getStatusEffects()) {
			player.networkHandler.sendPacket(new EntityPotionEffectS2CPacket(player.getEntityId(), statusEffect));
		}
	}

	/**
     * Run placement logic for the destination DimensionType.
     * If custom placement logic is provided during the method call, it is used instead of the dimension's logic.
     * destinationDimension is only a regular DimensionType when placementLogic is not null.
     * @param entity ServerPlayerEntity being teleported
     * @param placementLogic placement logic to run
     * @param destinationDimension destination FabricDimensionType
     * @param destinationWorld destination world
     */
    private static void runPlacementLogic(Entity entity, EntityPlacer placementLogic, DimensionType destinationDimension, ServerWorld destinationWorld) {
        if(destinationDimension instanceof FabricDimensionType) {
            if(placementLogic != null) {
                placementLogic.placeEntity(entity, destinationDimension, destinationWorld);
            } else {
                ((FabricDimensionType) destinationDimension).getEntryPlacement().placeEntity(entity, destinationDimension, destinationWorld);
            }
        }

        else {
            placementLogic.placeEntity(entity, destinationDimension, destinationWorld);
        }
    }

    /**
     * Refresh 3 values used to track information regarding the player.
     */
    private static void refreshTrackerValues(ServerPlayerEntity entity) {
        ((TeleportingServerPlayerEntity) entity).set13978(-1);
        ((TeleportingServerPlayerEntity) entity).set13997(-1f);
        ((TeleportingServerPlayerEntity) entity).set13979(-1);
    }

    /**
     * Sends packets related to LevelProperties:
     * - Player Respawn S2C
     * - Difficulty S2C
     *
     * @param dimensionType
     */
    private static void sendLevelPackets(ServerPlayerEntity entity, DimensionType dimensionType) {
        LevelProperties levelProperties = entity.world.getLevelProperties();
        entity.networkHandler.sendPacket(new PlayerRespawnS2CPacket(dimensionType, levelProperties.getGeneratorType(), entity.interactionManager.getGameMode()));
        entity.networkHandler.sendPacket(new DifficultyS2CPacket(levelProperties.getDifficulty(), levelProperties.isDifficultyLocked()));
    }
}
