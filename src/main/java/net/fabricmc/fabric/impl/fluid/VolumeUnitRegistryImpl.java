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
import net.fabricmc.fabric.api.fluid.VolumeUnit;
import net.fabricmc.fabric.api.fluid.VolumeUnitRegistry;
import net.minecraft.util.Identifier;

public class VolumeUnitRegistryImpl implements VolumeUnitRegistry {
    public static final VolumeUnitRegistryImpl INSTANCE = new VolumeUnitRegistryImpl();
    
    private static final Object2ObjectOpenHashMap<Identifier, VolumeUnitImpl> UNITS = new Object2ObjectOpenHashMap<>();
    
    /** Set to non-zero value on first retrieval */
    private long leastCommonMultiple = 0;
    
    private VolumeUnitRegistryImpl() {}
    
    @Override
    public synchronized VolumeUnit register(Identifier id, int unitsPerBucket) {
        if(unitsPerBucket <= 0) {
            throw new IllegalArgumentException("FluidUnit unitsPerBucket must be >= 1");
        } else if(leastCommonMultiple != 0) {
            throw new UnsupportedOperationException("FluidUnit registration attempted after initialization or another mod has "
                    + "instantiated a FluidMeter during initialization, prematurely freezing the FluidUnitRegistry.");
        }
        
        VolumeUnitImpl result = UNITS.get(id);
        if(result == null) {
            result = new VolumeUnitImpl(id, unitsPerBucket);
            UNITS.put(id, result);
        } else if (result.unitsPerBucket() != unitsPerBucket) {
            throw new IllegalStateException("FluidUnit with same identifer already exists with a different units per bucket.");
        }
        return result;
    }

    @Override
    public VolumeUnit get(Identifier id) {
        return UNITS.get(id);
    }
    
    long lcm() {
        long result = this.leastCommonMultiple;
        if(result == 0) {
            computeLcm();
            result = leastCommonMultiple;
        }
        return result;
    }
    
    private void computeLcm() {
        leastCommonMultiple = 1;
        UNITS.values().forEach(u -> {
            leastCommonMultiple = lcm(leastCommonMultiple, u.unitsPerBucket());
        });
    }
    
    private long lcm(long a, long b) {
        return a / gcd(a, b) * b;
    }
    
    private long gcd(long a, long b) {
        while(b != 0) {
           long t = b; 
           b = a % b; 
           a = t; 
        }
        return a;
    }
}
