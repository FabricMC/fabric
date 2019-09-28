package net.fabricmc.fabric.impl.resources;

import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackContainer;

import java.util.stream.Stream;

public interface CustomInjectionResourcePackProfile {

	static CustomInjectionResourcePackProfile from(ResourcePackContainer profile) {
		return (CustomInjectionResourcePackProfile) profile;
	}

	default Stream<? extends ResourcePack> injectPacks() {
		ResourcePack pack = ((ResourcePackContainer) this).createResourcePack();
		
		if (pack instanceof CustomInjectionResourcePack) {
			return ((CustomInjectionResourcePack) pack).injectPacks();
		}
		
		return Stream.of(pack);
	}
}
