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

package net.fabricmc.fabric.mixin.networking.client;

import com.mojang.blaze3d.systems.RenderSystem;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

/**
 * This mixin makes disconnect reason text scrollable.
 */
@Mixin(DisconnectedScreen.class)
public abstract class DisconnectedScreenMixin extends Screen {
	@Shadow
	private int reasonHeight;

	@Unique
	private int actualReasonHeight;

	@Unique
	private int scroll;

	@Unique
	private int maxScroll;

	private DisconnectedScreenMixin() {
		super(null);
	}

	// Inject to right after reasonHeight is stored, to make sure the back button have correct position.
	@Inject(method = "init", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/DisconnectedScreen;reasonHeight:I", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER))
	private void init(CallbackInfo ci) {
		actualReasonHeight = reasonHeight;
		reasonHeight = Math.min(reasonHeight, height - 100);
		scroll = 0;
		maxScroll = actualReasonHeight - reasonHeight;
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/MultilineText;drawCenterWithShadow(Lnet/minecraft/client/util/math/MatrixStack;II)I"))
	private int render(MultilineText instance, MatrixStack matrixStack, int x, int y) {
		DrawableHelper.enableScissor(0, y, width, y + reasonHeight);
		instance.drawCenterWithShadow(matrixStack, x, y - scroll);
		RenderSystem.disableScissor();

		// Draw gradient at the top/bottom to indicate that the text is scrollable.
		if (actualReasonHeight > reasonHeight) {
			int startX = (width - instance.getMaxWidth()) / 2;
			int endX = (width + instance.getMaxWidth()) / 2;

			if (scroll > 0) {
				fillGradient(matrixStack, startX, y, endX, y + 10, 0xFF000000, 0);
			}

			if (scroll < maxScroll) {
				fillGradient(matrixStack, startX, y + reasonHeight - 10, endX, y + reasonHeight, 0, 0xFF000000);
			}
		}

		return y + reasonHeight;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		scroll = MathHelper.clamp(scroll - (MathHelper.sign(amount) * client.textRenderer.fontHeight * 10), 0, maxScroll);
		return super.mouseScrolled(mouseX, mouseY, amount);
	}
}
