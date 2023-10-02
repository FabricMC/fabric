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

package net.fabricmc.fabric.mixin.screenlayer;

import static net.fabricmc.fabric.impl.client.screenlayer.ScreenLayerManager.SCREENS;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;

import net.fabricmc.fabric.impl.client.screenlayer.ScreenLayerManager;
import net.fabricmc.loader.api.FabricLoader;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	private DrawContext drawContext;

	@Redirect(method = "render",
			at = @At(value = "INVOKE",
					target = "Lcom/mojang/blaze3d/systems/RenderSystem;setProjectionMatrix(Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/systems/VertexSorter;)V"))
	public void render4f(Matrix4f matrix4f, VertexSorter sorting) {
		matrix4f = render4fTranslate();
		RenderSystem.setProjectionMatrix(matrix4f, sorting);
	}

	@Redirect(method = "render",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V"))
	public void renderTranslate(MatrixStack stack, float f, float g, float h) {
		renderTranslate(stack);
	}

	@ModifyVariable(method = "render",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/client/gui/screen/Screen;renderWithTooltip(Lnet/minecraft/client/gui/DrawContext;IIF)V",
					shift = At.Shift.BEFORE))
	public DrawContext renderDrawScreenPre(DrawContext drawContext) {
		this.drawContext = drawContext;
		drawScreen(drawContext);
		return drawContext;
	}

	@Inject(method = "render",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/client/gui/screen/Screen;renderWithTooltip(Lnet/minecraft/client/gui/DrawContext;IIF)V",
					shift = At.Shift.AFTER))
	public void renderDrawScreenPost(float f, long l, boolean bl, CallbackInfo ci) {
		drawScreenPost(this.drawContext.getMatrices());
	}

	void renderTranslate(MatrixStack stack) {
		stack.translate(0.0, 0.0, 1000.0 - ScreenLayerManager.getFarPlane());
	}

	void drawScreen(DrawContext drawContext) {
		float partialTick = MinecraftClient.getInstance().getLastFrameDuration();
		drawContext.getMatrices().push();
		SCREENS.forEach(layer -> {
			layer.render(drawContext, 0x7fffffff, 0x7fffffff, partialTick);
			drawContext.getMatrices().translate(0, 0, 2000);
		});
	}

	void drawScreenPost(MatrixStack stack) {
		stack.pop();
	}

	Matrix4f render4fTranslate() {
		Window window = MinecraftClient.getInstance().getWindow();
		return new Matrix4f()
				.ortho(0.0f,
						(float) ((double) window.getWidth() / window.getScaleFactor()),
						(float) ((double) window.getHeight() / window.getScaleFactor()),
						0.0f,
						1000.0f,
						ScreenLayerManager.getFarPlane(),
						FabricLoader.getInstance().isModLoaded("vulkanmod")); // GL needs false, Vulkan needs true. If mod is loaded, supply true.
	}
}
