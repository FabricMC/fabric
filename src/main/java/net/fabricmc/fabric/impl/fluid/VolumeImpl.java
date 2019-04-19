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

import net.fabricmc.fabric.api.fluid.Volume;
import net.fabricmc.fabric.api.fluid.VolumeUnit;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.util.PacketByteBuf;

class VolumeImpl implements Volume {
    private long buckets = 0;
    private long fraction = 0;
    
    VolumeImpl() {};
    
    @Override
    public long add(long amount, VolumeUnit unit) {
        if(buckets == Long.MAX_VALUE) {
            return 0;
        }
        
        final long lcm = VolumeUnitRegistryImpl.INSTANCE.lcm();
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
    public long remove(long amount, VolumeUnit unit) {
        final long lcm = VolumeUnitRegistryImpl.INSTANCE.lcm();
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
    public double total(VolumeUnit unit) {
        final int unitsPerBucket = unit.unitsPerBucket();
        return (double)buckets * unitsPerBucket + (double)fraction * unitsPerBucket / VolumeUnitRegistryImpl.INSTANCE.lcm();
    }

    @Override
    public long buckets() {
        return buckets;
    }

    @Override
    public long fraction(VolumeUnit unit) {
        return fraction * unit.unitsPerBucket() / VolumeUnitRegistryImpl.INSTANCE.lcm();
    }

    @Override
    public void set(long amount, VolumeUnit unit) {
        final long lcm = VolumeUnitRegistryImpl.INSTANCE.lcm();
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
    public boolean isVirtuallyEmpty(VolumeUnit unit) {
        return buckets == 0 && fraction(unit) == 0;
    }
    
    @Override
    public LongArrayTag toTag() {
        long[] data = new long[3];
        data[0] = buckets;
        data[1] = fraction;
        data[2] = VolumeUnitRegistryImpl.INSTANCE.lcm();
        return new LongArrayTag(data);
    }

    @Override
    public void fromTag(LongArrayTag tag) {
        long[] data = tag.getLongArray();
        buckets = data[0];
        
        final long lcm = VolumeUnitRegistryImpl.INSTANCE.lcm();
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
}
