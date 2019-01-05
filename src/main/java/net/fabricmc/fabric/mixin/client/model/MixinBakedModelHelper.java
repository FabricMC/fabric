package net.fabricmc.fabric.mixin.client.model;

import net.minecraft.util.math.Direction;

class MixinBakedModelHelper {
    /**
     * This is here for use by {@link MixinBakedModel} which cannot define
     * static fields without Mixin complaining.  The implementation there
     * relies on it avoid creating new Direction array instances in a hot loop.
     * 
     * TODO: ugly - where should this be?  Or do we not bother and just accept the overhead?
     */
    static final Direction[] DIRECTIONS = Direction.values();
}
