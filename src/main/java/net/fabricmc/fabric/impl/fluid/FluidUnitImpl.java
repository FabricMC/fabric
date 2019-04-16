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

import net.fabricmc.fabric.api.fluid.FluidUnit;
import net.minecraft.util.Identifier;

final class FluidUnitImpl implements FluidUnit {
    private final Identifier id;
    private final int unitsPerBucket;
        
    FluidUnitImpl(Identifier id, int unitsPerBucket) {
        this.id = id;
        this.unitsPerBucket = unitsPerBucket;
    }
    
    @Override
    public Identifier id() {
        return id;
    }

    @Override
    public int unitsPerBucket() {
        return unitsPerBucket;
    }
}
