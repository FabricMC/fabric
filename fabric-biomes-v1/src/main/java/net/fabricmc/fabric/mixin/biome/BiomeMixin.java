package net.fabricmc.fabric.mixin.biome;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.world.biome.Biome;

import net.fabricmc.fabric.impl.biome.HasBeenProcessedProvider;

@Mixin(Biome.class)
public class BiomeMixin implements HasBeenProcessedProvider {
	@Unique
	private boolean fabric_hasBeenProcessed = false;

	@Override
	public boolean hasBeenProcessed() {
		return fabric_hasBeenProcessed;
	}

	@Override
	public void setProcessed() {
		fabric_hasBeenProcessed = true;
	}
}
