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

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MagmaBlock;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShearsItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.test.GameTest;
import net.minecraft.test.TestContext;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;

public final class ShearsGameTest implements FabricGameTest, ModInitializer {
	public static final Item REAL_SHEARS = new ShearsItem(new FabricItemSettings().maxDamage(55)); // to show that ShearsItem will work
	public static final Item FAKE_SHEARS = new ArrowItem(new FabricItemSettings().maxDamage(38)); // to show that anything in fabric:shears will work
	public static final Item STACK_AWARE_SHEARS = new Item(new FabricItemSettings().maxDamage(122)) {
		@Override
		public boolean isShears(ItemStack stack) {
			return !stack.isDamaged(); // does not work as shears after getting damaged
		}
	};
	public static final Block SHEARS_ONLY = new MagmaBlock(AbstractBlock.Settings.create().breakInstantly()); // see the loot table for this block

	private static final BlockPos POS = new BlockPos(0, 1, 0);

	@Override
	public void onInitialize() {
		Registry.register(Registries.ITEM, new Identifier("fabric-item-api-v1-testmod", "real_shears"), REAL_SHEARS);
		Registry.register(Registries.ITEM, new Identifier("fabric-item-api-v1-testmod", "fake_shears"), FAKE_SHEARS);
		Registry.register(Registries.ITEM, new Identifier("fabric-item-api-v1-testmod", "stack_aware_shears"), STACK_AWARE_SHEARS);
		Registry.register(Registries.BLOCK, new Identifier("fabric-item-api-v1-testmod", "shears_only"), SHEARS_ONLY);
	}

	@GameTest(templateName = EMPTY_STRUCTURE)
	public void shearsHarvestTest(TestContext context) {
		testMineGrass(REAL_SHEARS.getDefaultStack(), context, true);
		testMineGrass(FAKE_SHEARS.getDefaultStack(), context, true);
		ItemStack stackAwareShears = STACK_AWARE_SHEARS.getDefaultStack();
		testMineGrass(stackAwareShears, context, true);
		stackAwareShears.setDamage(1);
		testMineGrass(stackAwareShears, context, false);

		// test that SHEARS_ONLY only works with vanilla shears
		Block.dropStacks(SHEARS_ONLY.getDefaultState(), context.getWorld(), POS, null, null, Items.SHEARS.getDefaultStack());
		context.waitAndRun(1, () -> context.expectItem(Items.ACACIA_LOG));
		context.killAllEntities();
		Block.dropStacks(SHEARS_ONLY.getDefaultState(), context.getWorld(), POS, null, null, REAL_SHEARS.getDefaultStack());
		context.waitAndRun(1, () -> context.dontExpectItem(Items.ACACIA_LOG));

		context.complete();
	}

	@GameTest(templateName = EMPTY_STRUCTURE)
	public void shearSheepTest(TestContext context) {
		PlayerEntity player = context.createMockSurvivalPlayer();
		testShearSheep(REAL_SHEARS.getDefaultStack(), player, context);
		testShearSheep(FAKE_SHEARS.getDefaultStack(), player, context);
		testShearSheep(STACK_AWARE_SHEARS.getDefaultStack(), player, context);
		testShearSheep(null, player, context);
		context.complete();
	}

	@GameTest(templateName = EMPTY_STRUCTURE)
	public void dispenserShearsTest(TestContext context) {
		context.setBlockState(POS, Blocks.DISPENSER.getDefaultState());
		DispenserBlockEntity blockEntity = (DispenserBlockEntity) Objects.requireNonNull(context.getBlockEntity(POS));
		testDispenserShears(REAL_SHEARS.getDefaultStack(), blockEntity, context);
		testDispenserShears(FAKE_SHEARS.getDefaultStack(), blockEntity, context);
		testDispenserShears(STACK_AWARE_SHEARS.getDefaultStack(), blockEntity, context);
		testDispenserShears(null, blockEntity, context);
		context.complete();
	}

	private void testMineGrass(ItemStack stack, TestContext context, boolean expectDrop) {
		Block.dropStacks(Blocks.SHORT_GRASS.getDefaultState(), context.getWorld(), POS, null, null, stack);
		context.waitAndRun(1, () -> {
			expectOrDont(context, Items.SHORT_GRASS, expectDrop);
			context.killAllEntities();
		});
	}

	private void testShearSheep(ItemStack stack, PlayerEntity player, TestContext context) {
		if (stack != null) {
			player.setStackInHand(Hand.MAIN_HAND, stack);
		}

		SheepEntity sheep = context.spawnEntity(EntityType.SHEEP, POS);
		player.interact(sheep, Hand.MAIN_HAND);
		expectOrDont(context, getWool(sheep), stack != null);
		sheep.kill();
		context.killAllEntities();
	}

	private void testDispenserShears(ItemStack stack, DispenserBlockEntity blockEntity, TestContext context) {
		if (stack != null) {
			blockEntity.setStack(0, stack);
		}

		SheepEntity sheep = context.spawnEntity(EntityType.SHEEP, POS.north());
		sheep.setAiDisabled(true);
		context.setBlockState(POS.up(), Blocks.REDSTONE_BLOCK);
		context.waitAndRun(1, () -> {
			context.setBlockState(POS.up(), Blocks.AIR);
			expectOrDont(context, getWool(sheep), stack != null);
			sheep.kill();
			context.killAllEntities();
		});
	}

	private void expectOrDont(TestContext context, Item item, boolean expectItem) {
		if (expectItem) {
			context.expectItem(item);
		} else {
			context.dontExpectItem(item);
		}
	}

	private Item getWool(SheepEntity sheep) {
		return Registries.ITEM.get(new Identifier(sheep.getColor().getName() + "_wool"));
	}
}
