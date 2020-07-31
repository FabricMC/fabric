package net.fabricmc.fabric.mixin.event.structure;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.structure.pool.FeaturePoolElement;
import net.minecraft.world.gen.feature.ConfiguredFeature;

@Mixin(FeaturePoolElement.class)
public interface FeaturePoolElementAccessor {
	@Accessor("feature")
	Supplier<ConfiguredFeature<?, ?>> getFeature();
}
