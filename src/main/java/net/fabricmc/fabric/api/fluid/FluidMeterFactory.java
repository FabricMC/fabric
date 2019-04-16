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

import net.fabricmc.fabric.impl.fluid.FluidMeterFactoryImpl;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.util.PacketByteBuf;

/**
 * Access to constructor and convenience deserializers.<p>
 * 
 * Note that instances should not be created during mod initialization
 * because fluid units may be registered in any sequence and instances
 * could become invalid if new units are registered after creation.
 */
public interface FluidMeterFactory {
    public static final FluidMeterFactory INSTANCE = FluidMeterFactoryImpl.INSTANCE;

    FluidMeter create();

    FluidMeter create(long startingAmount, FluidUnit units);

    FluidMeter fromTag(LongArrayTag tag);

    FluidMeter fromPacket(PacketByteBuf packetBuffer);
}
