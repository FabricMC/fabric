package net.fabricmc.fabric.impl.registry.sync;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.event.registry.RegistryAttributeRegistry;
import net.minecraft.util.registry.Registry;

public class FabricRegistryInit implements ModInitializer {
	@Override
	public void onInitialize() {
		//TODO this is a basic list of what should be done, a full list needs to be created

		RegistryAttributeRegistry.INSTANCE.registerAttributes(RegistryAttribute.SYNC,
				Registry.BLOCK,
				Registry.ITEM,
				Registry.BIOME
		);

		RegistryAttributeRegistry.INSTANCE.registerAttributes(RegistryAttribute.PERSISTENT,
				Registry.BIOME
		);
	}
}
