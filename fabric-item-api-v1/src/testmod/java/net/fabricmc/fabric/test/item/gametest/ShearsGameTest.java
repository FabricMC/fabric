package net.fabricmc.fabric.test.item.gametest;

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

import java.util.Objects;

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
		PlayerEntity player = context.createMockSurvivalPlayer();
		testMineGrass(REAL_SHEARS, player, context);
		testMineGrass(FAKE_SHEARS, player, context);
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
		blockEntity.setStack(0, REAL_SHEARS.getDefaultStack());
		SheepEntity sheep1 = context.spawnEntity(EntityType.SHEEP, POS.north());
		activateDispenser(context);
		context.expectItem(getWool(sheep1));
		sheep1.kill();
		blockEntity.setStack(0, REAL_SHEARS.getDefaultStack());
		SheepEntity sheep2 = context.spawnEntity(EntityType.SHEEP, POS.north());
		activateDispenser(context);
		context.expectItem(getWool(sheep2));
		context.complete();
	}

	private void testMineGrass(Item item, PlayerEntity player, TestContext context) {
		player.setStackInHand(Hand.MAIN_HAND, item.getDefaultStack());
		context.setBlockState(POS, Blocks.SHORT_GRASS);
		context.getWorld().breakBlock(context.getAbsolutePos(POS), true, player);
		context.expectItem(Items.SHORT_GRASS);
	}

	private void testShearSheep(Item item, PlayerEntity player, TestContext context) {
		SheepEntity sheep = context.spawnEntity(EntityType.SHEEP, POS);
		player.setStackInHand(Hand.MAIN_HAND, item.getDefaultStack());
		player.interact(sheep, Hand.MAIN_HAND);
		context.expectItem(getWool(sheep));
		sheep.kill();
	}

	private Item getWool(SheepEntity sheep) {
		return Registries.ITEM.get(new Identifier(sheep.getColor().getName() + "_wool"));
	}

	private void activateDispenser(TestContext context) {
		context.getWorld().scheduleBlockTick(context.getAbsolutePos(POS), Blocks.DISPENSER, 0);
	}
}
