package net.fabricmc.fabric.datafixer.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.fabricmc.fabric.impl.datafixer.FabricDataFixerImpl;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ChunkSerializer;

@Mixin(ChunkSerializer.class)
public class MixinChunkSerializer {
    
    @ModifyVariable(at = @At(value = "INVOKE", target = "net/minecraft/nbt/CompoundTag.putInt(Ljava/lang/String;I)V", ordinal = 0), method = "serialize(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/world/chunk/Chunk;)Lnet/minecraft/nbt/CompoundTag;", name = "compoundTag_1")
    private static CompoundTag addModDataVersions(CompoundTag input) {
        FabricDataFixerImpl.INSTANCE.addFixerVersions(input);
        return input;
    }
}
