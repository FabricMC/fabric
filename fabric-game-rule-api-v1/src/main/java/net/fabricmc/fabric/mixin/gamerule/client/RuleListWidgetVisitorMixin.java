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

package net.fabricmc.fabric.mixin.gamerule.client;

import java.util.Locale;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.world.GameRules;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.gamerule.v1.FabricGameRuleVisitor;
import net.fabricmc.fabric.api.gamerule.v1.rule.DoubleRule;
import net.fabricmc.fabric.api.gamerule.v1.rule.EnumRule;
import net.fabricmc.fabric.impl.gamerule.widget.DoubleRuleWidget;
import net.fabricmc.fabric.impl.gamerule.widget.EnumRuleWidget;

@Environment(EnvType.CLIENT)
@Mixin(targets = "net/minecraft/client/gui/screen/world/EditGameRulesScreen$RuleListWidget$1")
public abstract class RuleListWidgetVisitorMixin implements GameRules.TypeConsumer, FabricGameRuleVisitor {
	@Final
	@Shadow
	private EditGameRulesScreen field_24314;
	@Shadow
	protected abstract <T extends GameRules.Rule<T>> void createRuleWidget(GameRules.Key<T> key, EditGameRulesScreen.RuleWidgetFactory<T> ruleWidgetFactory);

	@Override
	public void visitDouble(GameRules.Key<DoubleRule> key, GameRules.Type<DoubleRule> type) {
		this.createRuleWidget(key, (name, description, ruleName, rule) -> {
			return new DoubleRuleWidget(this.field_24314, name, description, ruleName, rule);
		});
	}

	@Override
	public <E extends Enum<E>> void visitEnum(GameRules.Key<EnumRule<E>> key, GameRules.Type<EnumRule<E>> type) {
		this.createRuleWidget(key, (name, description, ruleName, rule) -> {
			return new EnumRuleWidget<>(this.field_24314, name, description, ruleName, rule, key.getTranslationKey());
		});
	}

	/**
	 * @reason We need to display an enum rule's default value as translated.
	 */
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/GameRules$Rule;serialize()Ljava/lang/String;"), method = "net/minecraft/client/gui/screen/world/EditGameRulesScreen$RuleListWidget$1.createRuleWidget(Lnet/minecraft/world/GameRules$Key;Lnet/minecraft/client/gui/screen/world/EditGameRulesScreen$RuleWidgetFactory;)V")
	private <T extends GameRules.Rule<T>> String displayProperEnumName(GameRules.Rule<T> rule, GameRules.Key<T> key, EditGameRulesScreen.RuleWidgetFactory<T> widgetFactory) {
		if (rule instanceof EnumRule) {
			String translationKey = key.getTranslationKey() + "." + ((EnumRule<?>) rule).get().name().toLowerCase(Locale.ROOT);

			if (I18n.hasTranslation(translationKey)) {
				return I18n.translate(translationKey);
			}

			return ((EnumRule<?>) rule).get().toString();
		}

		return rule.serialize();
	}
}
