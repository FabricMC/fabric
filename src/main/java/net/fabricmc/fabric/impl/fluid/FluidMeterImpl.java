package net.fabricmc.fabric.impl.fluid;

import net.fabricmc.fabric.api.fluid.FluidMeter;
import net.fabricmc.fabric.api.fluid.FluidUnit;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.util.PacketByteBuf;

class FluidMeterImpl implements FluidMeter {
    private long buckets = 0;
    private long fraction = 0;
    
    FluidMeterImpl() {};
    
    @Override
    public long add(long amount, FluidUnit unit) {
        if(buckets == Long.MAX_VALUE) {
            return 0;
        }
        
        final long lcm = FluidUnitRegistryImpl.INSTANCE.lcm();
        final long divisor = unit.unitsPerBucket();
        
        long bucketsIn = amount / divisor;
        long remainderUnitsIn = amount - (bucketsIn * divisor);
        
        if(remainderUnitsIn > 0) {
            fraction += remainderUnitsIn * (lcm / divisor);
            if(fraction > lcm) {
                buckets++;
                fraction -= lcm;
            }
        }
        
        bucketsIn = Math.min(bucketsIn, Long.MAX_VALUE - buckets);
        buckets += bucketsIn;
        return bucketsIn * divisor + remainderUnitsIn;
    }

    @Override
    public long remove(long amount, FluidUnit unit) {
        final long lcm = FluidUnitRegistryImpl.INSTANCE.lcm();
        final long divisor = unit.unitsPerBucket();
        
        long bucketsOut = amount / divisor;
        long partialUnitsOut = amount - (bucketsOut * divisor);
        
        bucketsOut = Math.min(bucketsOut, buckets);
        buckets -= bucketsOut;
        
        if(partialUnitsOut > 0) {
            long fractionOut = partialUnitsOut * (lcm / divisor);
            if(fractionOut > fraction && buckets > 0) {
                fraction += lcm;
                buckets--;
            } 
            fractionOut = Math.min(fraction, fractionOut);
            
            // prevent extraction of partial units
            partialUnitsOut = Math.min(partialUnitsOut, fractionOut / (lcm / divisor));
            if(partialUnitsOut > 0) {
                fraction -= partialUnitsOut * (lcm / divisor);
            }
        }
        
        return bucketsOut * divisor + partialUnitsOut;
    }

    @Override
    public double total(FluidUnit unit) {
        final int unitsPerBucket = unit.unitsPerBucket();
        return (double)buckets * unitsPerBucket + (double)fraction * unitsPerBucket / FluidUnitRegistryImpl.INSTANCE.lcm();
    }

    @Override
    public long buckets() {
        return buckets;
    }

    @Override
    public long fraction(FluidUnit unit) {
        return fraction * unit.unitsPerBucket() / FluidUnitRegistryImpl.INSTANCE.lcm();
    }

    @Override
    public void set(long amount, FluidUnit unit) {
        final long lcm = FluidUnitRegistryImpl.INSTANCE.lcm();
        final long divisor = unit.unitsPerBucket();
        buckets = amount / divisor;
        final long partialUnits = amount - (buckets * divisor);
        fraction = partialUnits * (lcm / divisor);
    }

    @Override
    public boolean isEmpty() {
        return buckets == 0 && fraction == 0;
    }

    @Override
    public LongArrayTag toTag() {
        long[] data = new long[3];
        data[0] = buckets;
        data[1] = fraction;
        data[2] = FluidUnitRegistryImpl.INSTANCE.lcm();
        return new LongArrayTag(data);
    }

    @Override
    public void fromTag(LongArrayTag tag) {
        long[] data = tag.getLongArray();
        buckets = data[0];
        
        final long lcm = FluidUnitRegistryImpl.INSTANCE.lcm();
        if(data[2] == lcm) {
            fraction = data[1];
        } else {
            fraction = (long) Math.floor((double)data[1] / data[2] * lcm);
        }
    }

    @Override
    public void toPacket(PacketByteBuf packetBuff) {
        packetBuff.writeLong(buckets);
        packetBuff.writeLong(fraction);
    }

    @Override
    public void fromPacket(PacketByteBuf packetBuff) {
        buckets = packetBuff.readLong();
        fraction = packetBuff.readLong();        
    }

    @Override
    public boolean isVirtualEmpty() {
        //TODO
        return false;
    }
}
