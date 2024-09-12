package net.fabricmc.fabric.impl.item;

import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.item.v1.EnchantmentSource;

import net.fabricmc.fabric.impl.resource.loader.BuiltinModResourcePackSource;
import net.fabricmc.fabric.impl.resource.loader.FabricResource;

import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;

import net.fabricmc.fabric.mixin.item.EnchantmentBuilderAccessor;

import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourcePackSource;

import java.util.List;

public class EnchantmentUtil {
	@SuppressWarnings("unchecked")
	public static Enchantment modify(RegistryKey<Enchantment> key, Enchantment originalEnchantment, EnchantmentSource source) {
		Enchantment.Builder builder = Enchantment.builder(originalEnchantment.definition());
		EnchantmentBuilderAccessor accessor = (EnchantmentBuilderAccessor) builder;

		builder.exclusiveSet(originalEnchantment.exclusiveSet());
		accessor.fabric_api$getEffectMap().addAll(originalEnchantment.effects());

		originalEnchantment.effects().stream()
				.forEach(component -> {
					if (component.value() instanceof List<?> valueList) {
						// component type cast is checked by the value
						accessor.fabric_api$getEffectsList((ComponentType<List<Object>>) component.type())
								.addAll(valueList);
					}
				});

		EnchantmentEvents.MODIFY.invoker().modify(key, builder, source);

		return new Enchantment(
				originalEnchantment.description(),
				accessor.fabric_api$getDefinition(),
				accessor.fabric_api$getExclusiveSet(),
				accessor.fabric_api$getEffectMap().build()
		);
	}

	public static EnchantmentSource determineSource(Resource resource) {
		if (resource != null) {
			ResourcePackSource packSource = ((FabricResource) resource).getFabricPackSource();

			if (packSource == ResourcePackSource.BUILTIN) {
				return EnchantmentSource.VANILLA;
			} else if (packSource == ModResourcePackCreator.RESOURCE_PACK_SOURCE || packSource instanceof BuiltinModResourcePackSource) {
				return EnchantmentSource.MOD;
			}
		}

		// If not builtin or mod, assume external data pack.
		// It might also be a virtual enchantment injected via mixin instead of being loaded
		// from a resource, but we can't determine that here.
		return EnchantmentSource.DATA_PACK;
	}

	private EnchantmentUtil() {

	}
}
