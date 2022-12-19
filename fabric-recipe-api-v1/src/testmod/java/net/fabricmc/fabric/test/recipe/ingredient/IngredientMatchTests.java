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

package net.fabricmc.fabric.test.recipe.ingredient;

import java.util.List;
import java.util.Objects;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.test.GameTest;
import net.minecraft.test.GameTestException;
import net.minecraft.test.TestContext;
import net.minecraft.text.Text;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.fabricmc.fabric.api.recipe.v1.ingredient.DefaultCustomIngredients;

public class IngredientMatchTests {
	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testAllIngredient(TestContext context) {
		Ingredient allIngredient = DefaultCustomIngredients.all(Ingredient.ofItems(Items.APPLE, Items.CARROT), Ingredient.ofItems(Items.STICK, Items.CARROT));

		assertEquals(1, allIngredient.getMatchingStacks().length);
		assertEquals(Items.CARROT, allIngredient.getMatchingStacks()[0].getItem());
		assertEquals(false, allIngredient.isEmpty());

		assertEquals(false, allIngredient.test(new ItemStack(Items.APPLE)));
		assertEquals(true, allIngredient.test(new ItemStack(Items.CARROT)));
		assertEquals(false, allIngredient.test(new ItemStack(Items.STICK)));

		Ingredient emptyAllIngredient = DefaultCustomIngredients.all(Ingredient.ofItems(Items.APPLE), Ingredient.ofItems(Items.STICK));

		assertEquals(0, emptyAllIngredient.getMatchingStacks().length);
		assertEquals(true, emptyAllIngredient.isEmpty());

		assertEquals(false, emptyAllIngredient.test(new ItemStack(Items.APPLE)));
		assertEquals(false, emptyAllIngredient.test(new ItemStack(Items.STICK)));

		context.complete();
	}

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testAnyIngredient(TestContext context) {
		Ingredient anyIngredient = DefaultCustomIngredients.any(Ingredient.ofItems(Items.APPLE, Items.CARROT), Ingredient.ofItems(Items.STICK, Items.CARROT));

		assertEquals(4, anyIngredient.getMatchingStacks().length);
		assertEquals(Items.APPLE, anyIngredient.getMatchingStacks()[0].getItem());
		assertEquals(Items.CARROT, anyIngredient.getMatchingStacks()[1].getItem());
		assertEquals(Items.STICK, anyIngredient.getMatchingStacks()[2].getItem());;
		assertEquals(Items.CARROT, anyIngredient.getMatchingStacks()[3].getItem());
		assertEquals(false, anyIngredient.isEmpty());

		assertEquals(true, anyIngredient.test(new ItemStack(Items.APPLE)));
		assertEquals(true, anyIngredient.test(new ItemStack(Items.CARROT)));
		assertEquals(true, anyIngredient.test(new ItemStack(Items.STICK)));

		context.complete();
	}

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testDifferenceIngredient(TestContext context) {
		Ingredient differenceIngredient = DefaultCustomIngredients.difference(Ingredient.ofItems(Items.APPLE, Items.CARROT), Ingredient.ofItems(Items.STICK, Items.CARROT));

		assertEquals(1, differenceIngredient.getMatchingStacks().length);
		assertEquals(Items.APPLE, differenceIngredient.getMatchingStacks()[0].getItem());
		assertEquals(false, differenceIngredient.isEmpty());

		assertEquals(true, differenceIngredient.test(new ItemStack(Items.APPLE)));
		assertEquals(false, differenceIngredient.test(new ItemStack(Items.CARROT)));
		assertEquals(false, differenceIngredient.test(new ItemStack(Items.STICK)));

		context.complete();
	}

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testNbtIngredient(TestContext context) {
		for (boolean strict : List.of(true, false)) {
			NbtCompound undamagedNbt = new NbtCompound();
			undamagedNbt.putInt(ItemStack.DAMAGE_KEY, 0);

			Ingredient nbtIngredient = DefaultCustomIngredients.nbt(Ingredient.ofItems(Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE), undamagedNbt, strict);

			assertEquals(2, nbtIngredient.getMatchingStacks().length);
			assertEquals(Items.DIAMOND_PICKAXE, nbtIngredient.getMatchingStacks()[0].getItem());
			assertEquals(Items.NETHERITE_PICKAXE, nbtIngredient.getMatchingStacks()[1].getItem());
			assertEquals(undamagedNbt, nbtIngredient.getMatchingStacks()[0].getNbt());
			assertEquals(undamagedNbt, nbtIngredient.getMatchingStacks()[1].getNbt());
			assertEquals(false, nbtIngredient.isEmpty());

			// Undamaged is fine
			assertEquals(true, nbtIngredient.test(new ItemStack(Items.DIAMOND_PICKAXE)));
			assertEquals(true, nbtIngredient.test(new ItemStack(Items.NETHERITE_PICKAXE)));

			// Damaged is not fine
			ItemStack damagedDiamondPickaxe = new ItemStack(Items.DIAMOND_PICKAXE);
			damagedDiamondPickaxe.setDamage(10);
			assertEquals(false, nbtIngredient.test(damagedDiamondPickaxe));

			// Renamed undamaged is only fine in partial matching
			ItemStack renamedUndamagedDiamondPickaxe = new ItemStack(Items.DIAMOND_PICKAXE);
			renamedUndamagedDiamondPickaxe.setCustomName(Text.literal("Renamed"));
			assertEquals(!strict, nbtIngredient.test(renamedUndamagedDiamondPickaxe));
		}

		// Also test strict null NBT matching
		Ingredient noNbtIngredient = DefaultCustomIngredients.nbt(Ingredient.ofItems(Items.APPLE), null, true);

		assertEquals(1, noNbtIngredient.getMatchingStacks().length);
		assertEquals(Items.APPLE, noNbtIngredient.getMatchingStacks()[0].getItem());
		assertEquals(null, noNbtIngredient.getMatchingStacks()[0].getNbt());
		assertEquals(false, noNbtIngredient.isEmpty());

		// No NBT is fine
		assertEquals(true, noNbtIngredient.test(new ItemStack(Items.APPLE)));

		// NBT is not fine
		ItemStack nbtApple = new ItemStack(Items.APPLE);
		nbtApple.setCustomName(Text.literal("Renamed"));
		assertEquals(false, noNbtIngredient.test(nbtApple));

		context.complete();
	}

	private static <T> void assertEquals(T expected, T actual) {
		if (!Objects.equals(expected, actual)) {
			throw new GameTestException(String.format("assertEquals failed%nexpected: %s%n but was: %s", expected, actual));
		}
	}
}
