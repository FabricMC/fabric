/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.mixin.client.renderer.registry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;

import net.fabricmc.fabric.api.client.rendereregistry.v1.item.ItemOverlayRendererRegistry;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {
	// This design means multiple durability bars are impossible. Too bad!

	@Unique private final MatrixStack matrixStack = new MatrixStack();
	@Unique private boolean needPopping = false;

	@Unique private void setGuiQuadColor(Args args, int color) {
		// renderGuiQuad takes each component separately, for some reason
		args.set(5, (color >> 16) & 0xFF);
		args.set(6, (color >> 8) & 0xFF);
		args.set(7, color & 0xFF);
		args.set(8, (color >> 24) & 0xFF);
	}

	// calls the pre-renderer, allows for cancelling the rest of the overlay
	@Inject(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
			at = @At("HEAD"), cancellable = true)
	public void preOverlay(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo ci) {
		if (stack.isEmpty()) {
			return;
		}

		matrixStack.push();
		boolean cancel = ItemOverlayRendererRegistry.getPreRenderer(stack.getItem()).renderOverlay(new MatrixStack(), renderer, stack, x, y, countLabel);
		matrixStack.pop();

		if (cancel) {
			ci.cancel();
		}
	}

	// why didn't Mojang just add a MatrixStack parameter? beats me
	@Redirect(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
			at = @At(value = "NEW", target = "net/minecraft/client/util/math/MatrixStack"))
	public MatrixStack reuseMatrixStack() {
		matrixStack.push();
		needPopping = true;
		return matrixStack;
	}

	// changes "is count label visible" condition
	// note - countLabel being non-null will *force* the label to be displayed
	@Redirect(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getCount()I", ordinal = 0))
	public int countVisible(ItemStack stack2, TextRenderer renderer, ItemStack stack, int x, int y, String countLabel) {
		return ItemOverlayRendererRegistry.getCountLabelProperties(stack.getItem()).isVisible(stack, countLabel) ? 2 : 1;
	}

	// changes count label contents and color
	@Redirect(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Ljava/lang/String;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)I"))
	public int countColor(TextRenderer textRenderer, String text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, boolean seeThrough, int backgroundColor, int light,
						TextRenderer textRenderer2, ItemStack stack, int x2, int y2, String countLabel) {
		return textRenderer.draw(ItemOverlayRendererRegistry.getCountLabelProperties(stack.getItem()).getContents(stack, countLabel).method_30937(),
				x, y, ItemOverlayRendererRegistry.getCountLabelProperties(stack.getItem()).getColor(stack, countLabel),
				shadow, matrix, vertexConsumers, seeThrough, backgroundColor, light);
	}

	// changes "is durability bar visible" condition
	@Redirect(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isDamaged()Z"))
	public boolean barVisible(ItemStack stack2, TextRenderer renderer, ItemStack stack) {
		return ItemOverlayRendererRegistry.getDurabilityBarProperties(stack.getItem()).isVisible(stack);
	}

	// changes durability bar fill factor and color
	@ModifyArgs(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderGuiQuad(Lnet/minecraft/client/render/BufferBuilder;IIIIIIII)V",
					ordinal = 1))
	public void barFillAndColor(Args args, TextRenderer renderer, ItemStack stack, int x, int y, String countLabel) {
		// set width
		args.set(3, Math.round(ItemOverlayRendererRegistry.getDurabilityBarProperties(stack.getItem()).getFillFactor(stack) * 13));
		// set color
		setGuiQuadColor(args, ItemOverlayRendererRegistry.getDurabilityBarProperties(stack.getItem()).getColor(stack));
	}

	// changes "is cooldown overlay visible" condition
	@Redirect(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ItemCooldownManager;getCooldownProgress(Lnet/minecraft/item/Item;F)F"))
	public float cooldownVisible(ItemCooldownManager itemCooldownManager, Item item, float partialTicks,
			TextRenderer renderer, ItemStack stack, int x, int y, String countLabel) {
		return ItemOverlayRendererRegistry.getCooldownOverlayProperties(item).isVisible(stack, MinecraftClient.getInstance()) ? 1 : 0;
	}

	// changes cooldown fill factor and color
	@ModifyArgs(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderGuiQuad(Lnet/minecraft/client/render/BufferBuilder;IIIIIIII)V",
					ordinal = 2))
	public void cooldownFillAndColor(Args args, TextRenderer renderer, ItemStack stack, int x, int y, String countLabel) {
		float fill = ItemOverlayRendererRegistry.getCooldownOverlayProperties(stack.getItem()).getFillFactor(stack, MinecraftClient.getInstance());
		// set y position
		args.set(2, y + MathHelper.floor(16 * (1 - fill)));
		// set height
		args.set(4, MathHelper.ceil(16 * fill));
		// set color
		setGuiQuadColor(args, ItemOverlayRendererRegistry.getCooldownOverlayProperties(stack.getItem()).getColor(stack, MinecraftClient.getInstance()));
	}

	// calls the post-renderer
	@Inject(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
			at = @At("TAIL"))
	public void postOverlay(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo ci) {
		if (stack.isEmpty()) {
			return;
		}

		if (needPopping) {
			matrixStack.pop();
			needPopping = false;
		}

		matrixStack.push();
		ItemOverlayRendererRegistry.getPostRenderer(stack.getItem()).renderOverlay(matrixStack, renderer, stack, x, y, countLabel);
		matrixStack.pop();
	}
}
