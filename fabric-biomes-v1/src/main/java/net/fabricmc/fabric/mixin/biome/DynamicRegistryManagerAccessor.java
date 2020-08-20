package net.fabricmc.fabric.mixin.biome;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.util.registry.DynamicRegistryManager;

@Mixin(DynamicRegistryManager.class)
public interface DynamicRegistryManagerAccessor {
	@Accessor("field_26733")
	static DynamicRegistryManager.Impl getBuiltin() {
		throw new AbstractMethodError();
	}
}
