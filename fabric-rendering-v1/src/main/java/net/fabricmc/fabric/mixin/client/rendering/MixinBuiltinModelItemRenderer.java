package net.fabricmc.fabric.mixin.client.rendering;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRenderer;
import net.fabricmc.fabric.impl.client.rendering.BuiltinItemRendererRegistryImpl;

@Mixin(BuiltinModelItemRenderer.class)
class MixinBuiltinModelItemRenderer {
	@Inject(method = "render", at = @At("RETURN"))
	private void fabric_onRender(ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo info) {
		BuiltinItemRenderer renderer = BuiltinItemRendererRegistryImpl.getRenderer(stack.getItem());

		if (renderer != null) {
			renderer.render(stack, matrices, vertexConsumers, light, overlay);
		}
	}
}
