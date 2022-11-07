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

import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.fabricmc.fabric.test.item.CustomDamageTest;

public class BrewingStandGameTest implements FabricGameTest {
	private static final int BREWING_TIME = 800;
	private static final BlockPos POS = new BlockPos(0, 1, 0);

	@GameTest(templateName = EMPTY_STRUCTURE)
	public void basicBrewing(TestContext context) {
		context.setBlockState(POS, Blocks.BREWING_STAND);
		BrewingStandBlockEntity blockEntity = (BrewingStandBlockEntity) Objects.requireNonNull(context.getBlockEntity(POS));

		loadFuel(blockEntity, context);

		prepareForBrewing(blockEntity, new ItemStack(Items.NETHER_WART, 8),
				PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER));

		brew(blockEntity, context);
		assertInventory(blockEntity, "Testing vanilla brewing.",
				PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD),
				PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD),
				PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD),
				new ItemStack(Items.NETHER_WART, 7),
				ItemStack.EMPTY);

		context.complete();
	}

	@GameTest(templateName = EMPTY_STRUCTURE)
	public void vanillaRemainderTest(TestContext context) {
		context.setBlockState(POS, Blocks.BREWING_STAND);
		BrewingStandBlockEntity blockEntity = (BrewingStandBlockEntity) Objects.requireNonNull(context.getBlockEntity(POS));

		loadFuel(blockEntity, context);

		prepareForBrewing(blockEntity, new ItemStack(Items.DRAGON_BREATH),
				PotionUtil.setPotion(new ItemStack(Items.SPLASH_POTION), Potions.AWKWARD));

		brew(blockEntity, context);
		assertInventory(blockEntity, "Testing vanilla brewing recipe remainder.",
				PotionUtil.setPotion(new ItemStack(Items.LINGERING_POTION), Potions.AWKWARD),
				PotionUtil.setPotion(new ItemStack(Items.LINGERING_POTION), Potions.AWKWARD),
				PotionUtil.setPotion(new ItemStack(Items.LINGERING_POTION), Potions.AWKWARD),
				new ItemStack(Items.GLASS_BOTTLE),
				ItemStack.EMPTY);

		context.complete();
	}

	@GameTest(templateName = EMPTY_STRUCTURE)
	public void fabricRemainderTest(TestContext context) {
		context.setBlockState(POS, Blocks.BREWING_STAND);
		BrewingStandBlockEntity blockEntity = (BrewingStandBlockEntity) Objects.requireNonNull(context.getBlockEntity(POS));

		loadFuel(blockEntity, context);

		prepareForBrewing(blockEntity, new ItemStack(CustomDamageTest.WEIRD_PICK),
				PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER));

		brew(blockEntity, context);
		assertInventory(blockEntity, "Testing fabric brewing recipe remainder.",
				PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD),
				PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD),
				PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD),
				RecipeGameTest.withDamage(new ItemStack(CustomDamageTest.WEIRD_PICK), 1),
				ItemStack.EMPTY);

		prepareForBrewing(blockEntity, RecipeGameTest.withDamage(new ItemStack(CustomDamageTest.WEIRD_PICK), 10),
				PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER));

		brew(blockEntity, context);
		assertInventory(blockEntity, "Testing fabric brewing recipe remainder.",
				PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD),
				PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD),
				PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD),
				RecipeGameTest.withDamage(new ItemStack(CustomDamageTest.WEIRD_PICK), 11),
				ItemStack.EMPTY);

		prepareForBrewing(blockEntity, RecipeGameTest.withDamage(new ItemStack(CustomDamageTest.WEIRD_PICK), 31),
				PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER));

		brew(blockEntity, context);
		assertInventory(blockEntity, "Testing fabric brewing recipe remainder.",
				PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD),
				PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD),
				PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD),
				ItemStack.EMPTY,
				ItemStack.EMPTY);

		context.complete();
	}

	private void prepareForBrewing(BrewingStandBlockEntity blockEntity, ItemStack ingredient, ItemStack potion) {
		blockEntity.setStack(0, potion.copy());
		blockEntity.setStack(1, potion.copy());
		blockEntity.setStack(2, potion.copy());
		blockEntity.setStack(3, ingredient);
	}

	private void assertInventory(BrewingStandBlockEntity blockEntity, String extraErrorInfo, ItemStack... stacks) {
		for (int i = 0; i < stacks.length; i++) {
			ItemStack currentStack = blockEntity.getStack(i);
			ItemStack expectedStack = stacks[i];

			RecipeGameTest.assertStacks(currentStack, expectedStack, extraErrorInfo);
		}
	}

	private void loadFuel(BrewingStandBlockEntity blockEntity, TestContext context) {
		blockEntity.setStack(4, new ItemStack(Items.BLAZE_POWDER));
		BrewingStandBlockEntity.tick(context.getWorld(), POS, context.getBlockState(POS), blockEntity);
	}

	private void brew(BrewingStandBlockEntity blockEntity, TestContext context) {
		for (int i = 0; i < BREWING_TIME; i++) {
			BrewingStandBlockEntity.tick(context.getWorld(), POS, context.getBlockState(POS), blockEntity);
		}
	}
}
