package net.fabricmc.fabric.mixin.datagen.loot;

import net.minecraft.data.server.loottable.BlockLootTableGenerator;

import net.minecraft.registry.RegistryWrapper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockLootTableGenerator.class)
public interface BlockLootTableGeneratorAccessor {

	@Accessor("field_51845")
	RegistryWrapper.WrapperLookup getWrapperLookup();

}
