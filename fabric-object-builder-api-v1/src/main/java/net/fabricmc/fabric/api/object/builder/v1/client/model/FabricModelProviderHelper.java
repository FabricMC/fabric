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

package net.fabricmc.fabric.api.object.builder.v1.client.model;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class FabricModelProviderHelper {
	public static void registerCrossbow(Item crossbow) {
		FabricModelPredicateProviderRegistry.register(crossbow, new Identifier("pull"), (itemStack4, clientWorld3, livingEntity2) -> {
			if (livingEntity2 == null) {
				return 0.0F;
			}

			return CrossbowItem.isCharged(itemStack4) ? 0.0F : (float) (itemStack4.getMaxUseTime() - livingEntity2.getItemUseTimeLeft()) / (float) CrossbowItem.getPullTime(itemStack4);
		});
		FabricModelPredicateProviderRegistry.register(crossbow, new Identifier("pulling"), (itemStack3, clientWorld1, livingEntity1) -> {
			if (livingEntity1 == null) {
				return 0.0F;
			}

			return livingEntity1.isUsingItem() && livingEntity1.getActiveItem() == itemStack3 && !CrossbowItem.isCharged(itemStack3) ? 1.0F : 0.0F;
		});
		FabricModelPredicateProviderRegistry.register(crossbow, new Identifier("charged"), (itemStack2, clientWorld4, livingEntity3) -> {
			if (livingEntity3 == null) {
				return 0.0F;
			}

			return CrossbowItem.isCharged(itemStack2) ? 1.0F : 0.0F;
		});

		FabricModelPredicateProviderRegistry.register(crossbow, new Identifier("firework"), (itemStack1, clientWorld2, livingEntity4) -> {
			if (livingEntity4 == null) {
				return 0.0F;
			}

			return CrossbowItem.isCharged(itemStack1) && CrossbowItem.hasProjectile(itemStack1, Items.FIREWORK_ROCKET) ? 1.0F : 0.0F;
		});
	}

	public static void registerBow(Item bow) {
		FabricModelPredicateProviderRegistry.register(bow, new Identifier("pull"), (itemStack, clientWorld, livingEntity) -> {
			if (livingEntity == null) {
				return 0.0F;
			}

			return livingEntity.getActiveItem() != itemStack ? 0.0F : (itemStack.getMaxUseTime() - livingEntity.getItemUseTimeLeft()) / 20.0F;
		});

		FabricModelPredicateProviderRegistry.register(bow, new Identifier("pulling"), (itemStack, clientWorld, livingEntity) -> {
			if (livingEntity == null) {
				return 0.0F;
			}

			return livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F;
		});
	}

	public static void registerElytra(Item elytra) {
		FabricModelPredicateProviderRegistry.register(elytra, new Identifier("broken"), (itemStack, clientWorld, livingEntity) -> {
			return ElytraItem.isUsable(itemStack) ? 0.0F : 1.0F;
		});
	}

	public static void registerFishingRod(Item fishingRod) {
		FabricModelPredicateProviderRegistry.register(fishingRod, new Identifier("cast"), (itemStack, clientWorld, livingEntity) -> {
			if (livingEntity == null) {
				return 0.0F;
			}

			boolean bl = livingEntity.getMainHandStack() == itemStack;
			boolean bl2 = livingEntity.getOffHandStack() == itemStack;

			if (livingEntity.getMainHandStack().getItem() instanceof FishingRodItem) {
				bl2 = false;
			}

			return (bl || bl2) && livingEntity instanceof PlayerEntity && ((PlayerEntity) livingEntity).fishHook != null ? 1.0F : 0.0F;
		});
	}

	public static void registerShield(Item shield) {
		FabricModelPredicateProviderRegistry.register(shield, new Identifier("blocking"), (itemStack, clientWorld, livingEntity) -> {
			return livingEntity != null && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F;
		});
	}

	public static void registerTridentModels(Item trident) {
		FabricModelPredicateProviderRegistry.register(trident, new Identifier("throwing"), (itemStack, clientWorld, livingEntity) -> {
			return livingEntity != null && livingEntity.isUsingItem() && livingEntity.getActiveItem() == itemStack ? 1.0F : 0.0F;
		});
	}
}
