package net.fabricmc.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.fabricmc.fabric.FabricDev;

import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Chunk.class)
public class ChunkMixin {
	@Dynamic("@ModifyExpressionValue's the FIELD GET of SharedConstants.isDevelopment to add a OR condition for FabricDev.ENABLE_UNPRIMED_HEIGHTMAP_LOGGING")
	@ModifyExpressionValue(method = "sampleHeightmap", at = @At(value = "FIELD", target = "Lnet/minecraft/SharedConstants;isDevelopment:Z"))
	private static boolean fabric$mevIsDevelopmentForDevModule(boolean original) {
		return original || FabricDev.ENABLE_UNPRIMED_HEIGHTMAP_LOGGING;
	}
}
