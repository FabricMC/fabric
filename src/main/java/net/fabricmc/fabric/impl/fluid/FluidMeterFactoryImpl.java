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
