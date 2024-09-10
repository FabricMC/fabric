package net.fabricmc.fabric.test.item;

import java.util.List;
import java.util.Optional;

import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.effect.EnchantmentEffectTarget;
import net.minecraft.enchantment.effect.TargetedEnchantmentEffect;
import net.minecraft.enchantment.effect.entity.IgniteEnchantmentEffect;
import net.minecraft.loot.condition.DamageSourcePropertiesLootCondition;
import net.minecraft.predicate.entity.DamageSourcePredicate;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;

public class CustomEnchantmentEffectsTest implements ModInitializer {
	@Override
	public void onInitialize() {
		EnchantmentEvents.MODIFY_EFFECTS.register(
				(key, effectsMap) -> {
					if (key == Enchantments.IMPALING) {
						effectsMap.add(
								EnchantmentEffectComponentTypes.POST_ATTACK,
								List.of(
										new TargetedEnchantmentEffect<>(
												EnchantmentEffectTarget.ATTACKER,
												EnchantmentEffectTarget.VICTIM,
												new IgniteEnchantmentEffect(EnchantmentLevelBasedValue.linear(4.0F)),
												Optional.of(
														DamageSourcePropertiesLootCondition
																.builder(DamageSourcePredicate.Builder.create().isDirect(true))
																.build()
												)
										)
								)
						);
					}
				}
		);
	}
}
