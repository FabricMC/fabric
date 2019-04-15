package net.fabricmc.fabric.impl.fluid;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.fluid.FluidUnit;
import net.fabricmc.fabric.api.fluid.FluidUnitRegistry;
import net.minecraft.util.Identifier;

public class FluidUnitRegistryImpl implements FluidUnitRegistry {
    public static final FluidUnitRegistryImpl INSTANCE = new FluidUnitRegistryImpl();
    
    private static final Object2ObjectOpenHashMap<Identifier, FluidUnitImpl> UNITS = new Object2ObjectOpenHashMap<>();
    
    /** Set to non-zero value on first retrieval */
    private long leastCommonMultiple = 0;
    
    private FluidUnitRegistryImpl() {}
    
    @Override
    public synchronized FluidUnit register(Identifier id, int unitsPerBucket) {
        if(unitsPerBucket <= 0) {
            throw new UnsupportedOperationException("FluidUnit unitsPerBucket must be >= 1");
        } else if(leastCommonMultiple != 0) {
            throw new UnsupportedOperationException("FluidUnit registration attempted after initialization or another mod has "
                    + "instantiated a FluidMeter during initialization, prematurely freezing the FluidUnitRegistry.");
        }
        
        FluidUnitImpl result = UNITS.get(id);
        if(result == null) {
            result = new FluidUnitImpl(id, unitsPerBucket);
            UNITS.put(id, result);
        } else if (result.unitsPerBucket() != unitsPerBucket) {
            throw new UnsupportedOperationException("FluidUnit with same identifer already exists with a different units per bucket.");
        }
        return result;
    }

    @Override
    public FluidUnit get(Identifier id) {
        return UNITS.get(id);
    }
    
    long lcm() {
        long result = this.leastCommonMultiple;
        if(result == 0) {
            computeLcm();
            result = leastCommonMultiple;
        }
        return result;
    }
    
    private void computeLcm() {
        leastCommonMultiple = 1;
        UNITS.values().forEach(u -> {
            leastCommonMultiple = lcm(leastCommonMultiple, u.unitsPerBucket());
        });
    }
    
    private long lcm(long a, long b) {
        return a / gcd(a, b) * b;
    }
    
    private long gcd(long a, long b) {
        while(b != 0) {
           long t = b; 
           b = a % b; 
           a = t; 
        }
        return a;
    }
}
