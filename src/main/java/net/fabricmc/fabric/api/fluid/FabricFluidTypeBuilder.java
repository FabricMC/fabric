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

package net.fabricmc.fabric.api.fluid;

import net.minecraft.util.Identifier;

public interface FabricFluidTypeBuilder {
    FabricFluidTypeBuilder translationKey(String key);
    
    FabricFluidTypeBuilder color(int color);
    
    FabricFluidTypeBuilder temperature(int kelvin);
    
    FabricFluidTypeBuilder density(int kgPerCubicMeter);
    
    /**
     * Builds, registers and returns a fluid type with the given id.
     * If the id is already registered will return null.
     */
    public FabricFluidType build(Identifier id);
}
