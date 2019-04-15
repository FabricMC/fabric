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
