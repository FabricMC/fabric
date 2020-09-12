package net.fabricmc.fabric.mixin.conditionalresource;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;

import net.fabricmc.fabric.impl.conditionalresource.NamespaceResourceManagerExtensions;
import net.fabricmc.fabric.impl.conditionalresource.WrappedResourcePack;

@Mixin(NamespaceResourceManager.class)
public abstract class NamespaceResourceManagerMixin implements NamespaceResourceManagerExtensions {
	@Shadow
	@Final
	protected List<ResourcePack> packList;
	@Shadow
	@Final
	private ResourceType type;
	@Shadow
	@Final
	private String namespace;

	@Override
	public void fabric_indexFabricMeta() {
		for (ResourcePack pack : this.packList) {
			if (pack instanceof WrappedResourcePack) {
				((WrappedResourcePack) pack).fabric_indexFabricMeta(this.type, this.namespace);
			}
		}
	}

	@ModifyVariable(method = "addPack", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private ResourcePack addPack(ResourcePack pack) {
		return new WrappedResourcePack(pack);
	}
}
