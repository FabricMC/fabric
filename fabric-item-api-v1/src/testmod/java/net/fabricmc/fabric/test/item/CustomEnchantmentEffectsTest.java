package net.fabricmc.fabric.test.item;

import java.util.Optional;

import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.effect.EnchantmentEffectEntry;
import net.minecraft.enchantment.effect.EnchantmentEffectTarget;
import net.minecraft.enchantment.effect.TargetedEnchantmentEffect;
import net.minecraft.enchantment.effect.entity.IgniteEnchantmentEffect;
import net.minecraft.enchantment.effect.value.AddEnchantmentEffect;
import net.minecraft.loot.condition.DamageSourcePropertiesLootCondition;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.DamageSourcePredicate;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;

import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.EntityTypePredicate;
import net.minecraft.registry.tag.EntityTypeTags;

public class CustomEnchantmentEffectsTest implements ModInitializer {
	@Override
	public void onInitialize() {
		EnchantmentEvents.MODIFY_EFFECTS.register(
				(key, builder) -> {
					if (key == Enchantments.IMPALING) {
						builder.getOrEmpty(EnchantmentEffectComponentTypes.POST_ATTACK)
								.add(
										new TargetedEnchantmentEffect<>(
												EnchantmentEffectTarget.ATTACKER,
												EnchantmentEffectTarget.VICTIM,
												new IgniteEnchantmentEffect(EnchantmentLevelBasedValue.linear(4.0F)),
												Optional.of(DamageSourcePropertiesLootCondition
														.builder(DamageSourcePredicate.Builder.create().isDirect(true))
														.build())
										)
								);

						builder.getOrEmpty(EnchantmentEffectComponentTypes.DAMAGE)
								.add(
										new EnchantmentEffectEntry<>(
												new AddEnchantmentEffect(
														EnchantmentLevelBasedValue.linear(2.5F)
												),
												Optional.of(
														EntityPropertiesLootCondition.builder(
																LootContext.EntityTarget.THIS,
																EntityPredicate.Builder.create()
																		.type(
																				EntityTypePredicate.create(EntityTypeTags.SENSITIVE_TO_BANE_OF_ARTHROPODS)
																		)
														).build()
												)
										)
								);
					}
				}
		);
	}
}
