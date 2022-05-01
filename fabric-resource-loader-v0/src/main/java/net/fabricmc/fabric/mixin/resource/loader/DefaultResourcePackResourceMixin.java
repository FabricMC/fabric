package net.fabricmc.fabric.mixin.resource.loader;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.resource.ResourcePackSource;

import net.fabricmc.fabric.api.resource.FabricResource;

@Mixin(targets = "net/minecraft/resource/DefaultResourcePack$1")
abstract class DefaultResourcePackResourceMixin implements FabricResource {
	@Override
	public ResourcePackSource getFabricPackSource() {
		return ResourcePackSource.PACK_SOURCE_BUILTIN;
	}
}
