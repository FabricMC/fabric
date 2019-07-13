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

import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;

import java.util.function.BiFunction;

/**
 * Fabric version of DimensionType.
 * DimensionType is a registry wrapper for Dimension.
 * Stores an EntityPlacer which is used to place entities in the world when they join.
 */
public class FabricDimensionType extends DimensionType
{
    private int numericalID;
    private Identifier identifier;
    private final EntityPlacer entryPlacement;

    public FabricDimensionType(Identifier name, int dimensionID, BiFunction<World, DimensionType, ? extends Dimension> factory, EntityPlacer entityPlacer)
    {
        super(dimensionID, name.getNamespace() + "_" + name.getPath(), "DIM_" + name.getPath(), factory, true);
        this.numericalID = dimensionID;
        this.identifier = name;
        this.entryPlacement = entityPlacer;
    }

    public int getNumericalIdentifier() {
        return numericalID;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public EntityPlacer getEntryPlacement() {
        return this.entryPlacement;
    }
}
