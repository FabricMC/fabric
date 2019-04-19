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


import net.fabricmc.fabric.api.fluid.Volume;
import net.fabricmc.fabric.api.fluid.VolumeFactory;
import net.fabricmc.fabric.api.fluid.VolumeUnit;
import net.fabricmc.fabric.api.fluid.VolumeUnitRegistry;
import net.minecraft.util.Identifier;

/**
 * Basic unit test for the Fluid API - not a mod.
 */
class VolumeUnitRegistryImplTest {

    @SuppressWarnings("unused")
    @Test
    void test() {
        VolumeUnitRegistry instance = VolumeUnitRegistry.INSTANCE;
        VolumeUnit microLiter = instance.register(new Identifier("fabric", "micro"), 1000000000);
        VolumeUnit quarter = instance.register(new Identifier("fabric", "quarter"), 4);
        VolumeUnit drop = instance.register(new Identifier("fabric", "drop"), 81 * 9);
        VolumeUnit level = instance.register(new Identifier("fabric", "level"), 16);
        
        Volume meter = VolumeFactory.INSTANCE.create();
        
        assert meter.add(1000, VolumeUnitRegistry.BUCKET) == 1000;
        assert meter.add(402, quarter) == 402;
        assert meter.buckets() == 1100;
        assert meter.fraction(quarter) == 2;
        assert meter.total(VolumeUnitRegistry.BUCKET) == 1100.5;
        
        assert meter.remove(3300, VolumeUnitRegistry.BOTTLE) == 3300;
        assert meter.buckets() == 0;
        assert !meter.isEmpty();
        assert meter.total(VolumeUnitRegistry.BUCKET) == 0.5;
        assert meter.remove(1, VolumeUnitRegistry.BOTTLE) == 1;
        assert meter.remove(1, VolumeUnitRegistry.BOTTLE) == 0;
        assert !meter.isEmpty();
        final long maxMicros = 1000000000 / 6;
        assert meter.remove(maxMicros, microLiter) == maxMicros;
        assert meter.isVirtuallyEmpty(microLiter);
        assert !meter.isEmpty();
    }

}
