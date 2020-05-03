package net.fabricmc.fabric.impl.registry.sync;

import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.event.registry.RegistryAttributeHolder;

public class FabricRegistryInit implements ModInitializer {
	@Override
	public void onInitialize() {
		RegistryAttributeHolder.get(Registry.BLOCK)
				.addAttribute(RegistryAttribute.SYNCED);

		RegistryAttributeHolder.get(Registry.ITEM)
				.addAttribute(RegistryAttribute.SYNCED);

		RegistryAttributeHolder.get(Registry.BIOME)
				.addAttribute(RegistryAttribute.SYNCED)
				.addAttribute(RegistryAttribute.PERSISTED);

		// StatusEffectInstance serialises with raw id
		RegistryAttributeHolder.get(Registry.STATUS_EFFECT)
				.addAttribute(RegistryAttribute.SYNCED)
				.addAttribute(RegistryAttribute.PERSISTED);
	}
}
