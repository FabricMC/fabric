package net.fabricmc.fabric.mixin.idremap;

import net.fabricmc.fabric.impl.dimension.DimensionIdsHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.UnmodifiableLevelProperties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(UnmodifiableLevelProperties.class)
public abstract class MixinUnmodifiableLevelProperties implements DimensionIdsHolder {
    @Shadow @Final private LevelProperties properties;

    /**
     * Delegates to the main level properties
     */
    @Override
    public CompoundTag fabric_getDimensionIds() {
        return ((DimensionIdsHolder) this.properties).fabric_getDimensionIds();
    }
}
