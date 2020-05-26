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

package net.fabricmc.fabric.impl.gamerule.widget;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.gamerule.v1.rule.DoubleRule;
import net.fabricmc.fabric.mixin.gamerule.client.EditGameRulesScreenAccessor;

@Environment(EnvType.CLIENT)
public class DoubleRuleWidget extends EditGameRulesScreen.AbstractRuleWidget {
	private final List<? extends Element> children;
	private final TextFieldWidget textFieldWidget;
	private final Text name;

	public DoubleRuleWidget(EditGameRulesScreen gameRuleScreen, Text name, List<Text> description, final String ruleName, DoubleRule rule) {
		gameRuleScreen.super(description);
		EditGameRulesScreenAccessor accessor = (EditGameRulesScreenAccessor) gameRuleScreen;
		this.name = name;

		this.textFieldWidget = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 10, 5, 42, 20,
				name.shallowCopy()
				.append("\n")
				.append(ruleName)
				.append("\n")
		);

		this.textFieldWidget.setText(Double.toString(rule.get()));
		this.textFieldWidget.setChangedListener(value -> {
			if (rule.validate(value)) {
				this.textFieldWidget.setEditableColor(14737632);
				accessor.callMarkValid(this);
			} else {
				this.textFieldWidget.setEditableColor(16711680);
				accessor.callMarkInvalid(this);
			}
		});

		this.children = ImmutableList.of(this.textFieldWidget);
	}

	@Override
	public List<? extends Element> children() {
		return this.children;
	}

	@Override
	public void render(MatrixStack matrixStack, int x, int y, int width, int height, int mouseX, int mouseY, int i, boolean bl, float delta) {
		MinecraftClient.getInstance().textRenderer.draw(matrixStack, this.name, width, (y + 5), 16777215);

		this.textFieldWidget.x = width + height - 44;
		this.textFieldWidget.y = y;
		this.textFieldWidget.render(matrixStack, mouseY, i, delta);
	}
}
