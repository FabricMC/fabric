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

import net.fabricmc.fabric.impl.fluid.VolumeUnitRegistryImpl;
import net.minecraft.util.Identifier;


public interface VolumeUnitRegistry {
    static final VolumeUnitRegistry INSTANCE = VolumeUnitRegistryImpl.INSTANCE;
    
    // Some potentially common units
    static final VolumeUnit BUCKET = INSTANCE.register(new Identifier("fabric", "bucket"), 1);
    static final VolumeUnit BOTTLE = INSTANCE.register(new Identifier("fabric", "bottle"), 3);
    static final VolumeUnit INGOT = INSTANCE.register(new Identifier("fabric", "ingot"), 9);
    static final VolumeUnit NUGGET = INSTANCE.register(new Identifier("fabric", "nugget"), 81);
    static final VolumeUnit LITER = INSTANCE.register(new Identifier("fabric", "liter"), 1000);
    
    /**
     * Creates new unit if it does not exist or returns existing unit if exact match found.
     * Throws IllegalStateException if a unit with the given identifier is already registered 
     * with a different value for unitsPerBucket.<p>
     * 
     * Must be called during mod initialization and not after.  The first instantiation of
     * {@link VolumeUnit} will cause the registry to become locked. Any call after that will 
     * throw UnsupporteOperationException.<p>
     * 
     * @param id
     * @param unitsPerBucket  Must be >= 1; will throw IllegalArgumentException otherwise
     * @return
     */
    VolumeUnit register(Identifier id, int unitsPerBucket);
    
    VolumeUnit get(Identifier id);
}