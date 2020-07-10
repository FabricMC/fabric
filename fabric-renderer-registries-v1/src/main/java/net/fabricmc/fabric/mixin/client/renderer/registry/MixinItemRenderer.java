package net.fabricmc.fabric.mixin.client.renderer.registry;

import net.fabricmc.fabric.api.client.rendereregistry.v1.ItemOverlayRenderer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.ItemOverlayRendererRegistry;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer {
	@Shadow public float zOffset;

	@Inject(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getCount()I", ordinal = 0),
			locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	public void on_renderGuiItemOverlay(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel,
										   CallbackInfo ci, MatrixStack matrixStack) {
		ItemOverlayRenderer ior = ItemOverlayRendererRegistry.INSTANCE.get(stack.getItem());
		if (ior != null && ior.renderOverlay(matrixStack, zOffset, renderer, stack, x, y, countLabel))
			ci.cancel();
	}
}
