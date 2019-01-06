package net.fabricmc.fabric.impl.client.model;

import net.fabricmc.fabric.mixin.client.model.MixinBakedModel;
import net.minecraft.util.math.Direction;

public class BakedModelMixinHelper {
    /**
     * This is here for use by {@link MixinBakedModel} which cannot define
     * non-private static fields without Mixin complaining.  The implementation there
     * relies on it to avoid potentially creation of new Direction array instances in a hot loop.<p>
     * 
     * This ugliness should be removed as soon as we can target J9 and define
     * private static members in interfaces.
     */
    public static final Direction[] DIRECTIONS = Direction.values();
}
