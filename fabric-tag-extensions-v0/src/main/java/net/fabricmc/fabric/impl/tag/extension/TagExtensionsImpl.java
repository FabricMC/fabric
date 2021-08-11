package net.fabricmc.fabric.impl.tag.extension;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public final class TagExtensionsImpl implements ModInitializer {
	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTING.register(TagFactoryImpl::loadDynamicRegistryTags);
	}
}
