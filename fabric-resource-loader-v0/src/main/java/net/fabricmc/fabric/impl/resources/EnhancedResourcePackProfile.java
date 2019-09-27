package net.fabricmc.fabric.impl.resources;

import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackContainer;
import net.minecraft.resource.ResourcePackCreator;

import java.util.List;
import java.util.function.Supplier;

public interface EnhancedResourcePackProfile {

	static EnhancedResourcePackProfile from(ResourcePackContainer profile) {
		return (EnhancedResourcePackProfile) profile;
	}

	List<Supplier<? extends ResourcePack>> getLowerPriorityPacks();
	List<Supplier<? extends ResourcePack>> getHigherPriorityPacks();
}
