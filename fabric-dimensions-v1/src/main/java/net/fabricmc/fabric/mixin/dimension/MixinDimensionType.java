package net.fabricmc.fabric.mixin.dimension;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.class_5363;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.dimension.DimensionTracker;
import net.minecraft.world.dimension.DimensionType;

import net.fabricmc.fabric.impl.dimension.FabricDimensionInternals;

@Mixin(DimensionType.class)
public class MixinDimensionType {
	@Inject(method = "method_28517", at = @At("RETURN"))
	private static void method_28517(long seed, CallbackInfoReturnable<SimpleRegistry<class_5363>> info) {
		SimpleRegistry<class_5363> dimensionRegistry = info.getReturnValue();

		for (FabricDimensionInternals.FabricDimensionOptions dimension : FabricDimensionInternals.FABRIC_DIMENSIONS) {
			if (dimensionRegistry.method_29723(dimension.getRegistryKey(Registry.field_25490))) {
				throw new RuntimeException("Duplicate dimension key " + dimension.identifier);
			}

			dimensionRegistry.add(dimension.getRegistryKey(Registry.field_25490), new class_5363(() -> dimension.dimensionType, dimension.chunkGeneratorFactory.create(seed)));
			dimensionRegistry.method_29725(dimension.getRegistryKey(Registry.field_25490));
		}
	}

	@Inject(method = "addDefaults", at = @At("RETURN"))
	private static void addDefaults(DimensionTracker.Modifiable tracker, CallbackInfoReturnable<DimensionTracker.Modifiable> info) {
		for (FabricDimensionInternals.FabricDimensionOptions dimension : FabricDimensionInternals.FABRIC_DIMENSIONS) {
			tracker.add(dimension.getRegistryKey(Registry.DIMENSION_TYPE_KEY), dimension.dimensionType);
		}
	}
}
