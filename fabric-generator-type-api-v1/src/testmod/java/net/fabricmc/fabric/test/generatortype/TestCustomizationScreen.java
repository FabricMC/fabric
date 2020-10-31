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

package net.fabricmc.fabric.test.generatortype;

import java.util.function.Consumer;

import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.generatortype.v1.FabricCustomizationScreen;

@Environment(EnvType.CLIENT)
final class TestCustomizationScreen extends FabricCustomizationScreen<TestGeneratorConfig> {
	private TextFieldWidget worldBlockField;
	private TextFieldWidget worldHeightField;
	private ButtonWidget doneButton;

	TestCustomizationScreen(CreateWorldScreen parent, Consumer<TestGeneratorConfig> configConsumer, TestGeneratorConfig config) {
		super(new TranslatableText("fabric.customize.test.title"), parent, configConsumer, config);
	}

	@Override
	protected void init() {
		this.worldBlockField = this.addButton(new TextFieldWidget(this.textRenderer, 50, 40, this.width - 100, 20, new LiteralText("")));
		this.worldBlockField.setText(Registry.BLOCK.getId(this.getGeneratorConfig().getWorldBlock()).toString());

		this.worldHeightField = this.addButton(new TextFieldWidget(this.textRenderer, 50, 60, this.width - 100, 20, new LiteralText("")));
		this.worldHeightField.setText(String.valueOf(this.getGeneratorConfig().getWorldHeight()));
		this.worldHeightField.setMaxLength(3);

		this.doneButton = this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, ScreenTexts.DONE, (buttonWidget) -> {
			this.setGeneratorConfig(new TestGeneratorConfig(Registry.BLOCK.get(Identifier.tryParse(this.worldBlockField.getText())), Integer.parseInt(worldHeightField.getText())));
			this.configConsumer.accept(this.getGeneratorConfig());
			this.onClose();
		}));
		this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, ScreenTexts.CANCEL, (buttonWidget) -> this.onClose()));
	}

	@Override
	public void tick() {
		this.worldBlockField.tick();
		this.worldHeightField.tick();

		boolean isWorldBlockCorrect, isWorldHeightCorrect;
		String worldHeightString = this.worldHeightField.getText();
		isWorldBlockCorrect = worldHeightString.length() != 0;

		try {
			int worldHeight = Integer.parseInt(worldHeightString);

			if (worldHeight > 256 || worldHeight < 0) {
				isWorldBlockCorrect = false;
			}
		} catch (NumberFormatException e) {
			isWorldBlockCorrect = false;
		}

		isWorldHeightCorrect = Registry.BLOCK.getOrEmpty(Identifier.tryParse(this.worldBlockField.getText())).isPresent();

		this.doneButton.active = isWorldBlockCorrect && isWorldHeightCorrect;

		super.tick();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
