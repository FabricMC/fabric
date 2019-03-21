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

import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;

import java.util.function.BiFunction;

public class FabricDimensionType extends DimensionType {

    private final EntityPlacer entityPlacer;

    protected FabricDimensionType(int id, String suffix, String saveDir, BiFunction<World, DimensionType, ? extends Dimension> dimensionFactory, boolean hasSkyLight, EntityPlacer entityPlacer) {
        super(id, suffix, saveDir, dimensionFactory, hasSkyLight);
        this.entityPlacer = entityPlacer;
    }

    public EntityPlacer getEntityPlacer() {
        return this.entityPlacer;
    }
}
