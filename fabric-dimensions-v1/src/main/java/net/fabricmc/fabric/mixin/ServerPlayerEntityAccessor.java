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

package net.fabricmc.fabric.mixin;

import net.fabricmc.fabric.impl.entity.TeleportingServerPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Used to set private values on ServerPlayerEntity.
 */
@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityAccessor extends Entity implements TeleportingServerPlayerEntity {

    private ServerPlayerEntityAccessor(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow protected abstract void method_18783(ServerWorld serverWorld_1);
    @Shadow private int field_13978;
    @Shadow private float field_13997;
    @Shadow private int field_13979;

    @Override
    public void handleDimensionCriterions(ServerWorld serverWorld) {
        this.method_18783(serverWorld);
    }

    @Override
    public void set13978(int set) {
        this.field_13978 = set;
    }

    @Override
    public void set13997(float set) {
        this.field_13997 = set;
    }

    @Override
    public void set13979(int set) {
        this.field_13979 = set;
    }
}
