package net.fabricmc.fabric.impl.registry.sync;

import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.event.registry.RegistryAttributeHolder;

public class FabricRegistryInit implements ModInitializer {
	@Override
	public void onInitialize() {
		RegistryAttributeHolder.get(Registry.BLOCK)
				.addAttribute(RegistryAttribute.SYNC);

		RegistryAttributeHolder.get(Registry.ITEM)
				.addAttribute(RegistryAttribute.SYNC);

		RegistryAttributeHolder.get(Registry.BIOME)
				.addAttribute(RegistryAttribute.SYNC)
				.addAttribute(RegistryAttribute.PERSISTENT);

		// StatusEffectInstance serialises with raw id
		RegistryAttributeHolder.get(Registry.STATUS_EFFECT)
				.addAttribute(RegistryAttribute.SYNC)
				.addAttribute(RegistryAttribute.PERSISTENT);
	}
}
