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

import net.minecraft.nbt.LongArrayTag;
import net.minecraft.util.PacketByteBuf;

/**
 * For use within a tank, machine, item or other game entity to track
 * the stored amount of a fluid or other volumetric substance measured
 * and transfered using {@link FluidUnit}.<p>
 * 
 * By design, it serves only to handle the accounting of contents.
 * Assumes all units tracked are for the <em>same</em> fluid. (Tracking
 * multiple fluids in the same device will require more than one {@link FluidMeter}.)
 * Does not know anything about the tracked fluid or its behavior.<p>
 * 
 * The enclosing device may constrain the units allowed for transfer in or out
 * depending on the fluid itself and expected use cases.<p>
 * 
 * The enclosing device is also responsible for transfer semantics such as capacity limits, 
 * simulated operations, etc.
 */
public interface FluidMeter {
    /**
     * Adds fluid to this instance, returning the amount added. The meter can track at 
     * least {@link Long#MAX_VALUE} buckets of fluid.  Any attempt to add more than the implemented 
     * limit will result in a partial addition.
     */
    long add(long amount, FluidUnit unit);
    
    /**
     * Removes fluid from this instance, up to the given amount. If the amount given exceeds
     * the present amount, only the present amount will be removed and that amount returned as the result.
     * (Negative amounts can never occur.)<p>
     */
    long remove(long amount, FluidUnit unit);
    
    /**
     * Returns the current fluid amount in the chosen unit. The integer portion of the return value
     * will be exact unless the total is larger than double precision can support. 
     */
    double total(FluidUnit unit);
    
    /**
     * Returns current fluid amount in buckets, excluding any fractional amount. Always exact. 
     */
    long buckets();
    
    /**
     * Returns any fractional amount not represented by {@link #buckets()} in the requested unit.
     * Result is rounded down when internal total is not a multiple of the requested unit.
     * Otherwise always exact.
     */
    long fraction(FluidUnit unit);
    
    /**
     * Sets the current fluid amount, discarding the existing contents.
     */
    void set(long amount, FluidUnit unit);
    
    /**
     * True if current fluid amount is exactly zero.
     */
    boolean  isEmpty();
    
    
    /**
     * True if the remaining amount is less than the given unit.  If the given unit is the 
     * smallest registered unit that can be extracted from this instance, then this instances
     * is effectively empty - nothing more can be taken from it. This will generally only happen 
     * when mixed units are add/removed to the same meter (like a tank). <p>
     * 
     * For example, a tank that has microliter resolution holding one bucket will have an extremely small 
     * amount remaining if two bottles are removed and then all whole remaining microliters are removed. 
     * If microliter is the smallest registered unit or at least the smallest unit recognized by the tank, 
     * the tank would then be virtually empty because the amount could never be removed.<p>
     * 
     * Such a tank might want to clear the tank at that point, (via {@link #set(long, FluidUnit)} or 
     * do so if a different fluid is input, depending on the intended usage of the tank. 
     */
    boolean isVirtuallyEmpty(FluidUnit unit);
    
    /** 
     * Serializes contents to Nbt. Includes the current
     * internal denominator for fractional units in case 
     * FluidUnit configuration changes on reload.
     */
    LongArrayTag toTag();
    
    /** 
     * Deserializes contents from Nbt. If FluidUnit configuration
     * is no longer compatible will attempt to convert - some fractional fluid 
     * may be lost when configuration is changed if the old amount cannot
     * be accurately represented in the new configuration.
     */
    void fromTag(LongArrayTag tag);
    
    /** 
     * Serializes contents to network packet buffer.
     * Assumes FluidUnit configuration is invariant on client and server.
     */
    void toPacket(PacketByteBuf packetBuff);
    
    /** 
     * Deserializes contents from network packet buffer.
     * Assumes FluidUnit configuration is invariant on client and server.
     */
    void fromPacket(PacketByteBuf packetBuff);
}
