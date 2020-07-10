package net.fabricmc.fabric.test.renderer_registries;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.ItemOverlayRendererRegistry;
import net.minecraft.item.Items;

public class FabricRendererRegistriesTest implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ItemOverlayRendererRegistry.add(Items.NETHERITE_SWORD, (matrixStack, renderer, stack, x, y, countLabel) -> {
			renderer.drawWithShadow(matrixStack, "yo", x, y, 0xFFFFFF);
			return false;
		});
		ItemOverlayRendererRegistry.add(Items.DIAMOND, (matrixStack, renderer, stack, x, y, countLabel) -> {
			renderer.drawWithShadow(matrixStack, "?", x + 17 - renderer.getWidth("?"), y + 9, 0xFFFFFF);
			return true;
		});
	}
}
