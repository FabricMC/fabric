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

package net.fabricmc.fabric.fluid;

import org.junit.jupiter.api.Test;


import net.fabricmc.fabric.api.fluid.FluidMeter;
import net.fabricmc.fabric.api.fluid.FluidMeterFactory;
import net.fabricmc.fabric.api.fluid.FluidUnit;
import net.fabricmc.fabric.api.fluid.FluidUnitRegistry;
import net.minecraft.util.Identifier;

/**
 * Basic unit test for the Fluid API - not a mod.
 */
class FluidUnitRegistryImplTest {

    @SuppressWarnings("unused")
    @Test
    void test() {
        FluidUnitRegistry instance = FluidUnitRegistry.INSTANCE;
        FluidUnit microLiter = instance.register(new Identifier("fabric", "micro"), 1000000000);
        FluidUnit quarter = instance.register(new Identifier("fabric", "quarter"), 4);
        FluidUnit drop = instance.register(new Identifier("fabric", "drop"), 81 * 9);
        FluidUnit level = instance.register(new Identifier("fabric", "level"), 16);
        
        FluidMeter meter = FluidMeterFactory.INSTANCE.create();
        
        assert meter.add(1000, FluidUnitRegistry.BUCKET) == 1000;
        assert meter.add(402, quarter) == 402;
        assert meter.buckets() == 1100;
        assert meter.fraction(quarter) == 2;
        assert meter.total(FluidUnitRegistry.BUCKET) == 1100.5;
        
        assert meter.remove(3300, FluidUnitRegistry.BOTTLE) == 3300;
        assert meter.buckets() == 0;
        assert !meter.isEmpty();
        assert meter.total(FluidUnitRegistry.BUCKET) == 0.5;
        assert meter.remove(1, FluidUnitRegistry.BOTTLE) == 1;
        assert meter.remove(1, FluidUnitRegistry.BOTTLE) == 0;
        assert !meter.isEmpty();
        final long maxMicros = 1000000000 / 6;
        assert meter.remove(maxMicros, microLiter) == maxMicros;
        assert meter.isVirtuallyEmpty(microLiter);
        assert !meter.isEmpty();
    }

}
