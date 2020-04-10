package net.fabricmc.fabric.api.client.rendering.v1;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Builtin item renderers render items with custom code.
 * They allow using non-model rendering, such as BERs, for items.
 *
 * <p>An item with a builtin renderer must have a model extending {@code minecraft:builtin/entity}.
 * The renderers are registered with {@link BuiltinItemRendererRegistry#register}.
 */
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface BuiltinItemRenderer {
	void render(ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay);
}
