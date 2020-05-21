package net.fabricmc.fabric.mixin.registry.sync;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.class_5321;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;

@Mixin(Registry.class)
public interface AccessorRegistry<T> {
	@Accessor(value = "field_25101")
	static MutableRegistry<MutableRegistry<?>> getRootRegistry() {
		throw new UnsupportedOperationException();
	}

	@Accessor(value = "field_25098")
	class_5321<Registry<T>> getRegistryKey();
}
