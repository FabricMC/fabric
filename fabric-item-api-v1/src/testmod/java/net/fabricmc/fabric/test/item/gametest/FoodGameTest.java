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

package net.fabricmc.fabric.test.item.gametest;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.FoodComponents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;

public final class FoodGameTest implements FabricGameTest, ModInitializer {
	public static final Item DAMAGE = Registry.register(Registries.ITEM, new Identifier("fabric-item-api-v1-testmod", "damage_food"), new DamageFood(new FabricItemSettings().maxDamage(20)));
	public static final Item NAME = Registry.register(Registries.ITEM, new Identifier("fabric-item-api-v1-testmod", "name_food"), new NameFood(new FabricItemSettings()));

	@GameTest(templateName = EMPTY_STRUCTURE)
	public void damageFoodTest(TestContext context) {
		PlayerEntity player = context.createMockSurvivalPlayer();
		HungerManager hungerManager = player.getHungerManager();

		for (int damage : new int[]{0, 1, 10, 19}) {
			hungerManager.add(-hungerManager.getFoodLevel(), 0.5f);
			ItemStack foodStack = DAMAGE.getDefaultStack();
			foodStack.setDamage(damage);
			player.eatFood(player.getWorld(), foodStack.copy());
			FoodComponent fc = Objects.requireNonNull(foodStack.getFoodComponent());
			int foodActual = hungerManager.getFoodLevel();
			int foodExpect = Math.min(20, fc.getHunger());
			context.assertTrue(foodActual == foodExpect, "damage=%d, food actual %d, expect %d".formatted(damage, foodActual, foodExpect));
			float satActual = hungerManager.getSaturationLevel();
			float satExpect = Math.min(foodExpect, fc.getHunger() * fc.getSaturationModifier() * 2);
			context.assertTrue(satActual == satExpect, "damage=%d, sat actual %f, expect %f".formatted(damage, satActual, satExpect));
		}

		context.complete();
	}

	@GameTest(templateName = EMPTY_STRUCTURE)
	public void nameFoodTest(TestContext context) {
		PlayerEntity player = context.createMockSurvivalPlayer();
		HungerManager hungerManager = player.getHungerManager();
		hungerManager.add(-hungerManager.getFoodLevel(), 0.5f);
		ItemStack foodStack = NAME.getDefaultStack();
		foodStack.setCustomName(Text.literal("enchanted_golden_apple"));
		player.eatFood(player.getWorld(), foodStack.copy());
		FoodComponent fc = FoodComponents.ENCHANTED_GOLDEN_APPLE;
		int foodActual = hungerManager.getFoodLevel();
		int foodExpect = Math.min(20, fc.getHunger());
		context.assertTrue(foodActual == foodExpect, "enchanted_golden_apple, food actual %d, expect %d".formatted(foodActual, foodExpect));
		float satActual = hungerManager.getSaturationLevel();
		float satExpect = Math.min(foodExpect, fc.getHunger() * fc.getSaturationModifier() * 2);
		context.assertTrue(satActual == satExpect, "enchanted_golden_apple, sat actual %f, expect %f".formatted(satActual, satExpect));
		context.complete();
	}

	@GameTest(templateName = EMPTY_STRUCTURE)
	public void nameMeatTest(TestContext context) {
		PlayerEntity player = context.createMockSurvivalPlayer();
		WolfEntity wolf = context.spawnEntity(EntityType.WOLF, context.getRelative(Vec3d.ZERO));
		wolf.setTamed(true);
		wolf.setOwner(player);
		wolf.setHealth(1f);
		ItemStack meat = NAME.getDefaultStack();
		meat.setCustomName(Text.of("mutton"));
		player.setStackInHand(Hand.MAIN_HAND, meat);
		player.interact(wolf, Hand.MAIN_HAND);
		float wolfHealth = wolf.getHealth();
		context.assertTrue(wolfHealth > 1, "actual %f, expect > 0".formatted(wolfHealth));
		context.complete();
	}

	//@GameTest(templateName = EMPTY_STRUCTURE)
	public void giveItems(TestContext context) {
		PlayerEntity player = context.getWorld().getPlayers().get(0);

		for (int damage : new int[]{0, 1, 10, 19}) {
			ItemStack foodStack = DAMAGE.getDefaultStack();
			foodStack.setDamage(damage);
			player.getInventory().offerOrDrop(foodStack);
		}

		ItemStack apple = NAME.getDefaultStack();
		apple.setCustomName(Text.literal("enchanted_golden_apple"));
		player.getInventory().offerOrDrop(apple);
		ItemStack mutton = NAME.getDefaultStack();
		mutton.setCustomName(Text.of("mutton"));
		player.getInventory().offerOrDrop(mutton);
		context.complete();
	}

	@Override
	public void onInitialize() {
	}

	public static class DamageFood extends Item {
		public DamageFood(Settings settings) {
			super(settings);
		}

		@Override
		public @Nullable FoodComponent getFoodComponent(ItemStack stack) {
			return new FoodComponent.Builder()
					.hunger(20 - 20 * stack.getDamage() / stack.getMaxDamage())
					.saturationModifier(0.5f)
					.build();
		}
	}

	public static class NameFood extends Item {
		public NameFood(Settings settings) {
			super(settings);
		}

		@Override
		public @Nullable FoodComponent getFoodComponent(ItemStack stack) {
			return Registries.ITEM.get(new Identifier(stack.getName().getString())).getFoodComponent(stack);
		}
	}
}
