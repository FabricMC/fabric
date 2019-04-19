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

package net.fabricmc.fabric.impl.fluid;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.fluid.FabricFluidType;
import net.fabricmc.fabric.api.fluid.FabricFluidTypeBuilder;
import net.fabricmc.fabric.api.fluid.FabricFluidTypeRegistry;
import net.minecraft.util.Identifier;

public class FabricFluidTypeRegistryImpl implements FabricFluidTypeRegistry {
    public static final FabricFluidTypeRegistryImpl INSTANCE = new FabricFluidTypeRegistryImpl();
    
    private static final Object2ObjectOpenHashMap<Identifier, FabricFluidType> TYPES = new Object2ObjectOpenHashMap<>();
    
    private FabricFluidTypeRegistryImpl() {}
    
    //TODO
    @Override
    public FabricFluidTypeBuilder builder() {
        return null;
    }

    @Override
    public FabricFluidType get(Identifier id) {
        return TYPES.get(id);
    }
}
