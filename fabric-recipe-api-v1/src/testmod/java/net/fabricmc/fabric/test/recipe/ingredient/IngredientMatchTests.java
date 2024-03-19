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

import java.util.Objects;

import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.test.GameTest;
import net.minecraft.test.GameTestException;
import net.minecraft.test.TestContext;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

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
	public void testComponentIngredient(TestContext context) {
		final Ingredient baseIngredient = Ingredient.ofItems(Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE, Items.STICK);
		final Ingredient undamagedIngredient = DefaultCustomIngredients.components(
				baseIngredient,
				builder -> builder.add(DataComponentTypes.DAMAGE, 0)
		);
		final Ingredient noNameUndamagedIngredient = DefaultCustomIngredients.components(
				baseIngredient,
				builder -> builder
						.add(DataComponentTypes.DAMAGE, 0)
						.remove(DataComponentTypes.CUSTOM_NAME)
		);

		ItemStack renamedUndamagedDiamondPickaxe = new ItemStack(Items.DIAMOND_PICKAXE);
		renamedUndamagedDiamondPickaxe.set(DataComponentTypes.CUSTOM_NAME, Text.literal("Renamed"));
		assertEquals(true, undamagedIngredient.test(renamedUndamagedDiamondPickaxe));
		assertEquals(false, noNameUndamagedIngredient.test(renamedUndamagedDiamondPickaxe));

		assertEquals(3, undamagedIngredient.getMatchingStacks().length);
		ItemStack result0 = undamagedIngredient.getMatchingStacks()[0];
		ItemStack result1 = undamagedIngredient.getMatchingStacks()[1];

		assertEquals(Items.DIAMOND_PICKAXE, result0.getItem());
		assertEquals(Items.NETHERITE_PICKAXE, result1.getItem());
		assertEquals(ComponentChanges.EMPTY, result0.getComponentChanges());
		assertEquals(ComponentChanges.EMPTY, result1.getComponentChanges());
		assertEquals(false, undamagedIngredient.isEmpty());

		// Undamaged is fine
		assertEquals(true, undamagedIngredient.test(new ItemStack(Items.DIAMOND_PICKAXE)));
		assertEquals(true, undamagedIngredient.test(new ItemStack(Items.NETHERITE_PICKAXE)));

		// Damaged is not fine
		ItemStack damagedDiamondPickaxe = new ItemStack(Items.DIAMOND_PICKAXE);
		damagedDiamondPickaxe.setDamage(10);
		assertEquals(false, undamagedIngredient.test(damagedDiamondPickaxe));

		// Checking for DAMAGE component requires the item is damageable in the first place
		assertEquals(false, undamagedIngredient.test(new ItemStack(Items.STICK)));

		// Custom data is strictly matched, like any other component with multiple fields
		final NbtCompound requiredData = new NbtCompound();
		requiredData.putInt("keyA", 1);
		final NbtCompound extraData = requiredData.copy();
		extraData.putInt("keyB", 2);

		final Ingredient customDataIngredient = DefaultCustomIngredients.components(
				baseIngredient,
				builder -> builder.add(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(requiredData))
		);
		ItemStack requiredDataStack = new ItemStack(Items.DIAMOND_PICKAXE);
		requiredDataStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(requiredData));
		ItemStack extraDataStack = new ItemStack(Items.DIAMOND_PICKAXE);
		extraDataStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(extraData));
		assertEquals(true, customDataIngredient.test(requiredDataStack));
		assertEquals(false, customDataIngredient.test(extraDataStack));

		// Default value is ignored in components(ItemStack)
		final Ingredient damagedPickaxeIngredient = DefaultCustomIngredients.components(renamedUndamagedDiamondPickaxe);
		ItemStack renamedDamagedDiamondPickaxe = renamedUndamagedDiamondPickaxe.copy();
		renamedDamagedDiamondPickaxe.setDamage(10);
		assertEquals(true, damagedPickaxeIngredient.test(renamedUndamagedDiamondPickaxe));
		assertEquals(true, damagedPickaxeIngredient.test(renamedDamagedDiamondPickaxe));

		context.complete();
	}

	@GameTest(templateName = FabricGameTest.EMPTY_STRUCTURE)
	public void testCustomDataIngredient(TestContext context) {
		final NbtCompound requiredNbt = Util.make(new NbtCompound(), nbt -> {
			nbt.putInt("keyA", 1);
		});
		final NbtCompound acceptedNbt = Util.make(requiredNbt.copy(), nbt -> {
			nbt.putInt("keyB", 2);
		});
		final NbtCompound rejectedNbt1 = Util.make(new NbtCompound(), nbt -> {
			nbt.putInt("keyA", -1);
		});
		final NbtCompound rejectedNbt2 = Util.make(new NbtCompound(), nbt -> {
			nbt.putInt("keyB", 2);
		});

		final Ingredient baseIngredient = Ingredient.ofItems(Items.STICK);
		final Ingredient customDataIngredient = DefaultCustomIngredients.customData(baseIngredient, requiredNbt);

		ItemStack stack = new ItemStack(Items.STICK);
		assertEquals(false, customDataIngredient.test(stack));
		stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(requiredNbt));
		assertEquals(true, customDataIngredient.test(stack));
		// This is a non-strict matching
		stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(acceptedNbt));
		assertEquals(true, customDataIngredient.test(stack));
		stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(rejectedNbt1));
		assertEquals(false, customDataIngredient.test(stack));
		stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(rejectedNbt2));
		assertEquals(false, customDataIngredient.test(stack));

		ItemStack[] matchingStacks = customDataIngredient.getMatchingStacks();
		assertEquals(1, matchingStacks.length);
		assertEquals(Items.STICK, matchingStacks[0].getItem());
		assertEquals(NbtComponent.of(requiredNbt), matchingStacks[0].get(DataComponentTypes.CUSTOM_DATA));

		context.complete();
	}

	private static <T> void assertEquals(T expected, T actual) {
		if (!Objects.equals(expected, actual)) {
			throw new GameTestException(String.format("assertEquals failed%nexpected: %s%n but was: %s", expected, actual));
		}
	}
}
