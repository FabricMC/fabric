package net.fabricmc.fabric.mixin.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.component.ComponentMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryInfo;

import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;

@Mixin(RegistryLoader.class)
class RegistryLoaderMixin {

	@WrapOperation(
			method = "parseAndAdd",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/registry/MutableRegistry;add(Lnet/minecraft/registry/RegistryKey;Ljava/lang/Object;Lnet/minecraft/registry/entry/RegistryEntryInfo;)Lnet/minecraft/registry/entry/RegistryEntry$Reference;"
			)
	)
	@SuppressWarnings("unchecked")
	private static <T> RegistryEntry.Reference<T> afterParse(
			MutableRegistry<T> instance,
			RegistryKey<T> registryKey,
			Object object,
			RegistryEntryInfo registryEntryInfo,
			Operation<RegistryEntry.Reference<T>> original
	) {
		if (object instanceof Enchantment enchantment) {
			ComponentMap.Builder builder = ComponentMap.builder();
			builder.addAll(enchantment.effects());
			EnchantmentEvents.MODIFY_EFFECTS.invoker().modifyEnchantmentEffects(
					(RegistryKey<Enchantment>) registryKey,
					builder
			);

			object = new Enchantment(
					enchantment.description(),
					enchantment.definition(),
					enchantment.exclusiveSet(),
					builder.build()
			);
		}
		return original.call(instance, registryKey, object, registryEntryInfo);
	}
}
