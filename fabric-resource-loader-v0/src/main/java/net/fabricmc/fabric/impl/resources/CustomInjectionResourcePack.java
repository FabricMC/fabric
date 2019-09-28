package net.fabricmc.fabric.impl.resources;

import net.minecraft.resource.ResourcePack;

import java.util.stream.Stream;

public interface CustomInjectionResourcePack extends ResourcePack {

	Stream<? extends ResourcePack> injectPacks();
}
