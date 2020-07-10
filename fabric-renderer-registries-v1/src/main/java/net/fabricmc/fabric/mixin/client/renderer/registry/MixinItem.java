package net.fabricmc.fabric.mixin.client.renderer.registry;

import net.fabricmc.fabric.api.client.rendereregistry.v1.ItemOverlayRenderer;
import net.fabricmc.fabric.impl.client.renderer.registry.ItemHooks;

import net.minecraft.item.Item;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
public class MixinItem implements ItemHooks {
	private ItemOverlayRenderer ior;

	@Override
	public ItemOverlayRenderer getItemOverlayRenderer() {
		return ior;
	}

	@Override
	public void setItemOverlayRenderer(ItemOverlayRenderer ior) {
		this.ior = ior;
	}
}
