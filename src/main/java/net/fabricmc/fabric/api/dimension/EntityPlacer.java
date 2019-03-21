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

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.dimension.DimensionType;

public abstract class EntityPlacer {

    public static final EntityPlacer DEFAULT_INSTACE = new EntityPlacer() {
        @Override
        public void placeEntity(Entity entity, DimensionType dimensionType, ServerWorld targetWorld) {
            //NO-OP
        }
    };

    public abstract void placeEntity(Entity entity, DimensionType dimensionType, ServerWorld targetWorld);

    protected void setEntityPosition(Entity entity, double x, double y, double z) {
        this.setEntityPosition(entity, x, y, z, entity.yaw, entity.pitch);
    }

    /**
     * used to set an entity's position, you MUST use this when repositioning the entity
     */
    protected void setEntityPosition(Entity entity, double x, double y, double z, float yaw, float pitch) {
        if(entity instanceof ServerPlayerEntity) {
            ((ServerPlayerEntity) entity).networkHandler.teleportRequest(x, y, z, yaw, pitch);
            ((ServerPlayerEntity) entity).networkHandler.syncWithPlayerPosition();
        }
        else {
            entity.setPositionAndAngles(x, y, z, yaw, pitch);
        }
    }
}
