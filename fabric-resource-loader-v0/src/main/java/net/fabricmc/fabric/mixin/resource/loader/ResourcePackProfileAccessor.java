package net.fabricmc.fabric.mixin.resource.loader;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;

@Mixin(ResourcePackProfile.class)
public interface ResourcePackProfileAccessor {
	@Accessor("source")
	ResourcePackSource getResourcePackSource();
}
