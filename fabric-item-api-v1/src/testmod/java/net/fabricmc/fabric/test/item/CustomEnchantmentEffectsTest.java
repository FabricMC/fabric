package net.fabricmc.fabric.test.item;

import java.util.Optional;

import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.effect.EnchantmentEffectEntry;
import net.minecraft.enchantment.effect.EnchantmentEffectTarget;
import net.minecraft.enchantment.effect.TargetedEnchantmentEffect;
import net.minecraft.enchantment.effect.entity.IgniteEnchantmentEffect;
import net.minecraft.enchantment.effect.value.AddEnchantmentEffect;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.condition.DamageSourcePropertiesLootCondition;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.DamageSourcePredicate;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;

import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.EntityTypePredicate;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.util.Identifier;

public class CustomEnchantmentEffectsTest implements ModInitializer {

	// weird impaling is a copy of impaling used for testing (just in case minecraft changes impaling for some reason)
	public static final RegistryKey<Enchantment> WEIRD_IMPALING = RegistryKey.of(
			RegistryKeys.ENCHANTMENT,
			Identifier.of("fabric-item-api-v1-testmod", "weird_impaling")
	);

	@Override
	public void onInitialize() {
		EnchantmentEvents.MODIFY_EFFECTS.register(
				(key, builder) -> {
					if (key == WEIRD_IMPALING) {

						// make impaling set things on fire
						builder.getOrEmpty(EnchantmentEffectComponentTypes.POST_ATTACK)
								.add(
										new TargetedEnchantmentEffect<>(
												EnchantmentEffectTarget.ATTACKER,
												EnchantmentEffectTarget.VICTIM,
												new IgniteEnchantmentEffect(EnchantmentLevelBasedValue.linear(4.0f)),
												Optional.of(DamageSourcePropertiesLootCondition
														.builder(DamageSourcePredicate.Builder.create().isDirect(true))
														.build())
										)
								);

						// add bonus impaling damage to zombie
						builder.getOrEmpty(EnchantmentEffectComponentTypes.DAMAGE)
								.add(
										new EnchantmentEffectEntry<>(
												new AddEnchantmentEffect(
														EnchantmentLevelBasedValue.linear(2.5f)
												),
												Optional.of(
														EntityPropertiesLootCondition.builder(
																LootContext.EntityTarget.THIS,
																EntityPredicate.Builder.create()
																		.type(
																				EntityTypePredicate.create(EntityType.ZOMBIE)
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
