package net.fabricmc.fabric.api.client.rendering.v1;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Dynamic item renderers render items with custom code.
 * They allow using non-model rendering, such as BERs, for items.
 *
 * <p>An item with a dynamic renderer must have a model extending {@code minecraft:builtin/entity}.
 * The renderers are registered with {@link BuiltinItemRendererRegistry#register(ItemConvertible, DynamicItemRenderer)}.
 */
@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface DynamicItemRenderer {
	/**
	 * Renders an item stack.
	 *
	 * @param stack           the rendered item stack
	 * @param mode            the model transformation mode
	 * @param matrices        the matrix stack
	 * @param vertexConsumers the vertex consumer provider
	 * @param light           packed lightmap coordinates
	 * @param overlay         the overlay UV passed to {@link net.minecraft.client.render.VertexConsumer#overlay(int)}
	 */
	void render(ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay);
}
