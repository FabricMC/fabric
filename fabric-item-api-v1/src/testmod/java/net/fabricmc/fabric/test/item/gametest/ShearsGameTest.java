package net.fabricmc.fabric.test.item.gametest;

import java.util.Objects;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
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

public class ShearsGameTest implements FabricGameTest, ModInitializer {
	public static final Item REAL_SHEARS = new ShearsItem(new FabricItemSettings().maxDamage(55)); // to show that ShearsItem will work
	public static final Item FAKE_SHEARS = new Item(new FabricItemSettings().maxDamage(38)); // to show that anything in fabric:shears will work
	public static final Item STACK_AWARE_SHEARS = new Item(new FabricItemSettings().maxDamage(122)) {
		@Override
		public boolean isShears(ItemStack stack) {
			return !stack.isDamaged(); // does not work as shears after getting damaged
		}
	};
	private static final BlockPos POS = new BlockPos(0, 1, 0);

	@Override
	public void onInitialize() {
		Registry.register(Registries.ITEM, new Identifier("fabric-item-api-v1-testmod", "real_shears"), REAL_SHEARS);
		Registry.register(Registries.ITEM, new Identifier("fabric-item-api-v1-testmod", "fake_shears"), FAKE_SHEARS);
		Registry.register(Registries.ITEM, new Identifier("fabric-item-api-v1-testmod", "stack_aware_shears"), STACK_AWARE_SHEARS);
	}

	@GameTest(templateName = EMPTY_STRUCTURE)
	public void harvestGrassTest(TestContext context) {
		context.waitAndRun(1, () -> testMineGrass(REAL_SHEARS.getDefaultStack(), context, true));
		context.waitAndRun(1, () -> testMineGrass(FAKE_SHEARS.getDefaultStack(), context, true));
		ItemStack stackAwareShears = STACK_AWARE_SHEARS.getDefaultStack();
		context.waitAndRun(1, () -> testMineGrass(stackAwareShears, context, true));
		stackAwareShears.setDamage(1);
		context.waitAndRun(1, () -> testMineGrass(stackAwareShears, context, false));
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
		context.setBlockState(POS, Blocks.DISPENSER.getDefaultState().with(DispenserBlock.TRIGGERED, true));
		DispenserBlockEntity blockEntity = (DispenserBlockEntity) Objects.requireNonNull(context.getBlockEntity(POS));
		context.waitAndRun(1, () -> testDispenserShears(REAL_SHEARS.getDefaultStack(), blockEntity, context));
		context.waitAndRun(1, () -> testDispenserShears(FAKE_SHEARS.getDefaultStack(), blockEntity, context));
		context.waitAndRun(1, () -> testDispenserShears(STACK_AWARE_SHEARS.getDefaultStack(), blockEntity, context));
		context.waitAndRun(1, () -> testDispenserShears(null, blockEntity, context));
		context.complete();
	}

	private void testMineGrass(ItemStack stack, TestContext context, boolean shouldPass) {
		Block.dropStacks(Blocks.SHORT_GRASS.getDefaultState(), context.getWorld(), POS, null, null, stack);
		context.waitAndRun(1, () -> {
			if (shouldPass) {
				context.expectItem(Items.SHORT_GRASS);
			} else {
				context.dontExpectItem(Items.SHORT_GRASS);
			}

			context.killAllEntities();
		});
	}

	private void testShearSheep(ItemStack stack, PlayerEntity player, TestContext context) {
		SheepEntity sheep = context.spawnEntity(EntityType.SHEEP, POS);

		if (stack != null) {
			player.setStackInHand(Hand.MAIN_HAND, stack);
		}

		player.interact(sheep, Hand.MAIN_HAND);

        if (stack == null) {
            context.dontExpectItem(getWool(sheep));
        } else {
            context.expectItem(getWool(sheep));
        }

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
			context.setBlockState(POS.up(), Blocks.REDSTONE_BLOCK);

            if (stack == null) {
                context.dontExpectItem(getWool(sheep));
            } else {
                context.expectItem(getWool(sheep));
            }

            sheep.kill();
			context.killAllEntities();
		});
	}

	private Item getWool(SheepEntity sheep) {
		return Registries.ITEM.get(new Identifier(sheep.getColor().getName() + "_wool"));
	}
}
