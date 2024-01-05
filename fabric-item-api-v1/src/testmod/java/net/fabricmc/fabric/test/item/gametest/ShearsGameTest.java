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
	public static final Item REAL_SHEARS = new ShearsItem(new FabricItemSettings().maxDamage(38)); // to show that ShearsItem will work
	public static final Item FAKE_SHEARS = new Item(new FabricItemSettings().maxDamage(38)); // to show that anything in fabric:shears will work
	private static final BlockPos POS = new BlockPos(0, 1, 0);

	@Override
	public void onInitialize() {
		Registry.register(Registries.ITEM, new Identifier("fabric-item-api-v1-testmod", "real_shears"), REAL_SHEARS);
		Registry.register(Registries.ITEM, new Identifier("fabric-item-api-v1-testmod", "fake_shears"), FAKE_SHEARS);
	}

	@GameTest(templateName = EMPTY_STRUCTURE)
	public void harvestGrassTest(TestContext context) {
		context.addTask(() -> testMineGrass(REAL_SHEARS, context));
		context.addTask(() -> testMineGrass(FAKE_SHEARS, context));
		context.complete();
	}

	@GameTest(templateName = EMPTY_STRUCTURE)
	public void shearSheepTest(TestContext context) {
		PlayerEntity player = context.createMockSurvivalPlayer();
		testShearSheep(REAL_SHEARS, player, context);
		testShearSheep(FAKE_SHEARS, player, context);
		context.complete();
	}

	@GameTest(templateName = EMPTY_STRUCTURE)
	public void dispenserShearsTest(TestContext context) {
		context.setBlockState(POS, Blocks.DISPENSER.getDefaultState().with(DispenserBlock.TRIGGERED, true));
		DispenserBlockEntity blockEntity = (DispenserBlockEntity) Objects.requireNonNull(context.getBlockEntity(POS));
		context.addTask(() -> testDispenserShears(REAL_SHEARS, blockEntity, context));
		context.addTask(() -> testDispenserShears(FAKE_SHEARS, blockEntity, context));
		context.complete();
	}

	private void testMineGrass(Item item, TestContext context) {
		Block.dropStacks(Blocks.SHORT_GRASS.getDefaultState(), context.getWorld(), POS, null, null, item.getDefaultStack());
		context.addTask(() -> context.expectItem(Items.SHORT_GRASS));
	}

	private void testShearSheep(Item item, PlayerEntity player, TestContext context) {
		SheepEntity sheep = context.spawnEntity(EntityType.SHEEP, POS);
		player.setStackInHand(Hand.MAIN_HAND, item.getDefaultStack());
		player.interact(sheep, Hand.MAIN_HAND);
		context.expectItem(getWool(sheep));
		sheep.kill();
	}

	private void testDispenserShears(Item item, DispenserBlockEntity blockEntity, TestContext context) {
		blockEntity.setStack(0, item.getDefaultStack());
		SheepEntity sheep = context.spawnEntity(EntityType.SHEEP, POS.north());
		context.putAndRemoveRedstoneBlock(POS.up(), 1);
		context.addTask(() -> {
			context.expectItem(getWool(sheep));
			sheep.kill();
		});
	}

	private Item getWool(SheepEntity sheep) {
		return Registries.ITEM.get(new Identifier(sheep.getColor().getName() + "_wool"));
	}
}
