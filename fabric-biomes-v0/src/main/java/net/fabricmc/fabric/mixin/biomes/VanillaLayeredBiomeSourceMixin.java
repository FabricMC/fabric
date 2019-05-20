package net.fabricmc.fabric.mixin.biomes;

import java.util.Set;
import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.fabric.impl.biomes.BiomeLists;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.gen.feature.StructureFeature;

@Mixin(VanillaLayeredBiomeSource.class)
public class VanillaLayeredBiomeSourceMixin
{
	@Inject(at = @At("HEAD"), method = "hasStructureFeature", cancellable = true)
	private void hasStructureFeature(StructureFeature<?> structureFeature_1, CallbackInfoReturnable<Boolean> info)
	{
		Function<StructureFeature<?>, Boolean> b = (structureFeature_1x) -> {
			
			Set<Biome> var2 = BiomeLists.CUSTOM_BIOMES;
			
			for(Biome biome_1 : var2)
			{
				if (biome_1.hasStructureFeature(structureFeature_1x))
				{
					return true;
				}
			}

			return false;
		};
		
		if (b.apply(structureFeature_1).booleanValue())
			info.setReturnValue(Boolean.TRUE);
	}
}
