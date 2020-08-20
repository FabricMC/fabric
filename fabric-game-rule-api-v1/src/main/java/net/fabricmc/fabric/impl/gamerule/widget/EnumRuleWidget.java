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
import java.util.Locale;

import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.gamerule.v1.rule.EnumRule;

@Environment(EnvType.CLIENT)
public final class EnumRuleWidget<E extends Enum<E>> extends EditGameRulesScreen.NamedRuleWidget {
	private final ButtonWidget buttonWidget;
	private final String rootTranslationKey;

	public EnumRuleWidget(EditGameRulesScreen gameRuleScreen, Text name, List<OrderedText> description, final String ruleName, EnumRule<E> rule, String translationKey) {
		gameRuleScreen.super(description, name);

		// Base translation key needs to be set before the button widget is created.
		this.rootTranslationKey = translationKey;
		this.buttonWidget = new ButtonWidget(10, 5, 88, 20, this.getValueText(rule.get()), (buttonWidget) -> {
			rule.cycle();
			buttonWidget.setMessage(this.getValueText(rule.get()));
		});

		this.children.add(this.buttonWidget);
	}

	public Text getValueText(E value) {
		final String key = this.rootTranslationKey + "." + value.name().toLowerCase(Locale.ROOT);

		if (I18n.hasTranslation(key)) {
			return new TranslatableText(key);
		}

		return new LiteralText(value.toString());
	}

	public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		// FIXME: Param names nightmare
		this.drawName(matrices, y, x);

		this.buttonWidget.x = x + entryWidth - 89;
		this.buttonWidget.y = y;
		this.buttonWidget.render(matrices, mouseX, mouseY, tickDelta);
	}
}
