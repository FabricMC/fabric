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

package net.fabricmc.fabric.mixin.client.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.GameRenderer;

import net.fabricmc.fabric.api.client.rendering.v1.CrosshairRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

@Mixin(InGameHud.class)
public class MixinInGameHud extends DrawableHelper {
	@Shadow
	private int scaledWidth;

	@Shadow
	private int scaledHeight;

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderColor(FFFF)V"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;render(Lnet/minecraft/client/util/math/MatrixStack;ILnet/minecraft/scoreboard/Scoreboard;Lnet/minecraft/scoreboard/ScoreboardObjective;)V")))
	public void render(MatrixStack matrixStack, float tickDelta, CallbackInfo callbackInfo) {
		HudRenderCallback.EVENT.invoker().onHudRender(matrixStack, tickDelta);
	}

	@Inject(method = "renderCrosshair", at = @At(value = "INVOKE", target = "com/mojang/blaze3d/systems/RenderSystem.blendFuncSeparate (Lcom/mojang/blaze3d/platform/GlStateManager$SrcFactor;Lcom/mojang/blaze3d/platform/GlStateManager$DstFactor;Lcom/mojang/blaze3d/platform/GlStateManager$SrcFactor;Lcom/mojang/blaze3d/platform/GlStateManager$DstFactor;)V"), cancellable = true)
	public void renderCrosshair(MatrixStack matrices, CallbackInfo ci) {
		boolean result = CrosshairRenderCallback.EVENT.invoker().onCrosshairRender(matrices, this.scaledWidth, this.scaledHeight, this.getZOffset());
		// Reset these in case they are changed
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);
		RenderSystem.enableBlend();

		// Disable crosshair rendering if needed
		if (!result) {
			ci.cancel();
		}
	}
}
