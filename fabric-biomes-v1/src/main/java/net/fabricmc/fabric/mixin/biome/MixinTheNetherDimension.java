package net.fabricmc.fabric.mixin.biome;

import java.util.HashSet;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.class_4767;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.TheNetherDimension;

import net.fabricmc.fabric.impl.biome.NetherBiomesImpl;

@Mixin(TheNetherDimension.class)
public class MixinTheNetherDimension {
	@Redirect(method = "createChunkGenerator", at = @At(value = "INVOKE", target = "net/minecraft/class_4767.method_24404(Ljava/util/Set;)Lnet/minecraft/class_4767;"))
	protected class_4767 redirect(class_4767 data, Set<Biome> set) {
		Set<Biome> newSet = new HashSet<>(set);
		newSet.addAll(NetherBiomesImpl.getNetherBiomes());
		data.method_24404(newSet);
		return data;
	}
}
