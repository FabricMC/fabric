package net.fabricmc.fabric.mixin.item;

import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.enchantment.Enchantment;

import net.minecraft.registry.entry.RegistryEntryList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(Enchantment.Builder.class)
public interface EnchantmentBuilderAccessor {
	@Accessor("definition")
	Enchantment.Definition fabric_api$getDefinition();

	@Accessor("exclusiveSet")
	RegistryEntryList<Enchantment> fabric_api$getExclusiveSet();

	@Accessor("effectMap")
	ComponentMap.Builder fabric_api$getEffectMap();

	@Invoker("getEffectsList")
	<E> List<E> fabric_api$getEffectsList(ComponentType<List<E>> type);
}
