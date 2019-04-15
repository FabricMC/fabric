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
