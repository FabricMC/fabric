package net.fabricmc.fabric.mixin.idremap;

import com.mojang.datafixers.DataFixer;
import net.fabricmc.fabric.impl.dimension.DimensionIdsFixer;
import net.fabricmc.fabric.impl.dimension.DimensionIdsHolder;
import net.fabricmc.fabric.impl.registry.RemapException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelProperties.class)
public abstract class MixinLevelProperties implements DimensionIdsHolder {
    @Unique
    private CompoundTag fabricDimensionIds = new CompoundTag();

    @Override
    public CompoundTag fabric_getDimensionIds() {
        return fabricDimensionIds;
    }

    @Inject(method = "<init>(Lnet/minecraft/nbt/CompoundTag;Lcom/mojang/datafixers/DataFixer;ILnet/minecraft/nbt/CompoundTag;)V", at = @At("RETURN"))
    private void readDimensionIds(CompoundTag data, DataFixer fixer, int version, CompoundTag player, CallbackInfo ci) {
        CompoundTag savedIds = data.getCompound("fabric_DimensionIds");
        try {
            this.fabricDimensionIds = DimensionIdsFixer.apply(savedIds);
        } catch (RemapException e) {
            throw new RuntimeException("Failed to assign unique dimension ids!", e);
        }
    }

    @Inject(method = "updateProperties", at = @At("RETURN"))
    private void writeDimensionIds(CompoundTag data, CompoundTag player, CallbackInfo ci) {
        data.put("fabric_DimensionIds", fabricDimensionIds);
    }
}
