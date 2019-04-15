package net.fabricmc.fabric.api.fluid;

import net.minecraft.util.Identifier;

/**
 * A fluid unit is a fraction of one "bucket" that may be transfered to or from an
 * object that inputs or outputs fluid using this API.<p>
 * 
 * All such transfers <em>must</em> happen using a registered unit.  This ensures
 * lossless transfers and reduces the likelihood of (but cannot prevent) orphaned
 * amounts that cannot be usefully consolidated or consumed.<p>
 * 
 * A "bucket" is conventionally equivalent to one Minecraft block or 1 cubic meter.
 * It is a unit of volume and can be used to measure the space consumed by true fluids, 
 * gases, or flowable solids.<p>
 * 
 * Note that transfer of whole buckets is always supported and there is no need/ability
 * to register units that are larger than one bucket.
 */
public interface FluidUnit {
    Identifier id();
    
    int unitsPerBucket();
}
