package net.fabricmc.fabric.mixin.datagen.loot;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.registry.RegistryWrapper;

@Mixin(BlockLootTableGenerator.class)
public interface BlockLootTableGeneratorAccessor {
	@Accessor()
	RegistryWrapper.WrapperLookup getRegistryLookup();
}
