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
import org.spongepowered.asm.mixin.injection.ModifyVariable;
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
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;

import net.fabricmc.fabric.api.client.rendereregistry.v1.item.ItemCooldownOverlayInfo;
import net.fabricmc.fabric.api.client.rendereregistry.v1.item.ItemDamageBarInfo;
import net.fabricmc.fabric.api.client.rendereregistry.v1.item.ItemLabelInfo;
import net.fabricmc.fabric.api.client.rendereregistry.v1.item.ItemOverlayRenderer;
import net.fabricmc.fabric.impl.client.renderer.registry.item.ItemOverlayMaps;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {
	// This design means multiple durability bars are impossible. Too bad!
	// TODO EDIT: Multiple durability bars actually *are* possible with a simple @Inject, I reckon...

	@Unique private final MatrixStack matrixStack = new MatrixStack();
	@Unique private boolean needPopping = false;
	@Unique private String countLabelTmp;

	@Unique private void setGuiQuadColor(Args args, int color) {
		// renderGuiQuad takes each component separately because :mojank:
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

		ItemOverlayRenderer.Pre preRenderer = ItemOverlayMaps.PRE_RENDERER_MAP.get(stack.getItem());

		if (preRenderer == null) {
			return;
		}

		matrixStack.push();
		boolean cancel = preRenderer.renderOverlay(new MatrixStack(), renderer, stack, x, y, countLabel);
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

	// hack to make the "countLabel != null" expression in the "is count label visible" condition always evaluate to false
	// this makes count label visibility depend on ItemStack.getCount(), which gets redirected to our isVisible method
	// thanks, @Gimpansor
	@ModifyVariable(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getCount()I", ordinal = 0), ordinal = 0)
	public String countVisibleCondHack(String countLabel, TextRenderer renderer, ItemStack stack) {
		countLabelTmp = countLabel;

		// only perform this hack if we override default behavior!
		if (ItemOverlayMaps.LABEL_INFO_MAP.get(stack.getItem()) == null) {
			return countLabel;
		}

		return null;
	}

	// changes "is count label visible" condition
	@Redirect(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getCount()I", ordinal = 0))
	public int countVisible(ItemStack stack2, TextRenderer renderer, ItemStack stack, int x, int y, String countLabel) {
		ItemLabelInfo props = ItemOverlayMaps.LABEL_INFO_MAP.get(stack.getItem());

		if (props == null) {
			return stack2.getCount();
		}

		return props.isVisible(stack, countLabel) ? 2 : 1;
	}

	// undoes the "countLabel != null" expression hack
	@ModifyVariable(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(DDD)V"), ordinal = 0)
	public String countVisibleCondHackUndo(String countLabel) {
		return countLabelTmp;
	}

	// changes count label contents and color
	@Redirect(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Ljava/lang/String;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)I"))
	public int countColor(TextRenderer textRenderer, String text, float x, float y, int color, boolean shadow, Matrix4f matrix, VertexConsumerProvider vertexConsumers, boolean seeThrough, int backgroundColor, int light,
						TextRenderer textRenderer2, ItemStack stack, int x2, int y2, String countLabel) {
		ItemLabelInfo props = ItemOverlayMaps.LABEL_INFO_MAP.get(stack.getItem());

		if (props == null) {
			return textRenderer.draw(text, x, y, color, shadow, matrix, vertexConsumers, seeThrough, backgroundColor, light);
		}

		Text contents = ItemOverlayMaps.LABEL_INFO_MAP.get(stack.getItem()).getContents(stack, countLabel);
		color = ItemOverlayMaps.LABEL_INFO_MAP.get(stack.getItem()).getColor(stack, countLabel);
		return textRenderer.draw(contents.method_30937(), x, y, color, shadow, matrix, vertexConsumers, seeThrough, backgroundColor, light);
	}

	// undoes the "countLabel != null" expression hack *again* (since the 1st undo only happens if the count label is rendered)
	@ModifyVariable(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isDamaged()Z"), ordinal = 0)
	public String countVisibleCondHackUndoAgain(String countLabel) {
		return countLabelTmp;
	}

	// changes "is damage bar visible" condition
	@Redirect(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isDamaged()Z"))
	public boolean barVisible(ItemStack stack) {
		ItemDamageBarInfo props = ItemOverlayMaps.DAMAGE_BAR_INFO_MAP.get(stack.getItem());

		if (props == null) {
			return stack.isDamageable();
		}

		return props.isVisible(stack);
	}

	// changes damage bar fill factor and color
	@ModifyArgs(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderGuiQuad(Lnet/minecraft/client/render/BufferBuilder;IIIIIIII)V",
					ordinal = 1))
	public void barFillAndColor(Args args, TextRenderer renderer, ItemStack stack, int x, int y, String countLabel) {
		ItemDamageBarInfo props = ItemOverlayMaps.DAMAGE_BAR_INFO_MAP.get(stack.getItem());

		if (props == null) {
			return;
		}

		// set width
		args.set(3, Math.round(props.getFillFactor(stack) * 13));
		// set color
		setGuiQuadColor(args, props.getColor(stack));
	}

	// changes "is cooldown overlay visible" condition
	@Redirect(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ItemCooldownManager;getCooldownProgress(Lnet/minecraft/item/Item;F)F"))
	public float cooldownVisible(ItemCooldownManager itemCooldownManager, Item item, float partialTicks,
			TextRenderer renderer, ItemStack stack) {
		ItemCooldownOverlayInfo props = ItemOverlayMaps.COOLDOWN_OVERLAY_INFO_MAP.get(item);

		if (props == null) {
			return itemCooldownManager.getCooldownProgress(item, partialTicks);
		}

		return props.isVisible(stack, MinecraftClient.getInstance()) ? 1 : 0;
	}

	// changes cooldown overlay fill factor and color
	@ModifyArgs(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/ItemRenderer;renderGuiQuad(Lnet/minecraft/client/render/BufferBuilder;IIIIIIII)V",
					ordinal = 2))
	public void cooldownFillAndColor(Args args, TextRenderer renderer, ItemStack stack, int x, int y) {
		ItemCooldownOverlayInfo props = ItemOverlayMaps.COOLDOWN_OVERLAY_INFO_MAP.get(stack.getItem());

		if (props == null) {
			return;
		}

		float fill = props.getFillFactor(stack, MinecraftClient.getInstance());
		// set y position
		args.set(2, y + MathHelper.floor(16 * (1 - fill)));
		// set height
		args.set(4, MathHelper.ceil(16 * fill));
		// set color
		setGuiQuadColor(args, props.getColor(stack, MinecraftClient.getInstance()));
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

		ItemOverlayRenderer.Post postRenderer = ItemOverlayMaps.POST_RENDERER_MAP.get(stack.getItem());

		if (postRenderer == null) {
			return;
		}

		matrixStack.push();
		postRenderer.renderOverlay(matrixStack, renderer, stack, x, y, countLabel);
		matrixStack.pop();
	}
}
