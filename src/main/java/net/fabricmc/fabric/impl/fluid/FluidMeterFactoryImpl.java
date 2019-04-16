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

import net.fabricmc.fabric.api.fluid.FluidMeter;
import net.fabricmc.fabric.api.fluid.FluidMeterFactory;
import net.fabricmc.fabric.api.fluid.FluidUnit;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.util.PacketByteBuf;

public class FluidMeterFactoryImpl implements FluidMeterFactory {
    public static final FluidMeterFactoryImpl INSTANCE = new FluidMeterFactoryImpl();
    
    private FluidMeterFactoryImpl() {}
    
    @Override
    public FluidMeter create() {
        return new FluidMeterImpl();
    }
    
    @Override
    public FluidMeter create(long startingAmount, FluidUnit units) {
        FluidMeterImpl result = new FluidMeterImpl();
        result.add(startingAmount, units);
        return result;
    }
    
    @Override
    public FluidMeter fromTag(LongArrayTag tag) {
        FluidMeterImpl result = new FluidMeterImpl();
        result.fromTag(tag);
        return result;
    }
    
    @Override
    public FluidMeter fromPacket(PacketByteBuf packetBuffer) {
        FluidMeterImpl result = new FluidMeterImpl();
        result.fromPacket(packetBuffer);
        return result;
    }
}
