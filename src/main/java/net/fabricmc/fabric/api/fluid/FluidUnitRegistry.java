package net.fabricmc.fabric.api.fluid;

import net.fabricmc.fabric.impl.fluid.FluidUnitRegistryImpl;
import net.minecraft.util.Identifier;


public interface FluidUnitRegistry {
    static final FluidUnitRegistry INSTANCE = FluidUnitRegistryImpl.INSTANCE;
    
    // Some potentially common units - these don't have to be the API - mostly for illustration
    static final FluidUnit BUCKET = INSTANCE.register(new Identifier("fabric", "bucket"), 1);
    static final FluidUnit BOTTLE = INSTANCE.register(new Identifier("fabric", "bottle"), 3);
    static final FluidUnit INGOT = INSTANCE.register(new Identifier("fabric", "ingot"), 9);
    static final FluidUnit NUGGET = INSTANCE.register(new Identifier("fabric", "nugget"), 81);
    static final FluidUnit LITER = INSTANCE.register(new Identifier("fabric", "liter"), 1000);
    
    /**
     * Creates new unit if it does not exist or returns existing unit if exact match found.
     * Throws an error if a unit with the given identifier is registered with a different value.<p>
     * 
     * Must be called during mod initialization and not after.  The first instantiation of any
     * {@link FluidUnit} instance will cause the registry to become locked. Any call after that
     * will result in an UnsupporteOperationException.<p>
     * 
     * @param id
     * @param unitsPerBucket  Must be >= 1; will throw UnsupportedOperationException otherwise
     * @return
     */
    FluidUnit register(Identifier id, int unitsPerBucket);
    
    FluidUnit get(Identifier id);
}