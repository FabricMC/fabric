package net.fabricmc.fabric.test.transfer.fluid;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class FluidTransferTest implements ModInitializer {
	public static final String MODID = "fabric-transfer-api-v1-testmod";

	private static final Block INFINITE_WATER_SOURCE = new Block(AbstractBlock.Settings.of(Material.METAL));
	private static final Block INFINITE_LAVA_SOURCE = new Block(AbstractBlock.Settings.of(Material.METAL));
	private static final Block FLUID_CHUTE = new FluidChuteBlock();
	public static BlockEntityType<FluidChuteBlockEntity> FLUID_CHUTE_TYPE;

	@Override
	public void onInitialize() {
		registerBlock(INFINITE_WATER_SOURCE, "infinite_water_source");
		registerBlock(INFINITE_LAVA_SOURCE, "infinite_lava_source");
		registerBlock(FLUID_CHUTE, "fluid_chute");

		FLUID_CHUTE_TYPE = BlockEntityType.Builder.create(FluidChuteBlockEntity::new, FLUID_CHUTE).build(null);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MODID, "fluid_chute"), FLUID_CHUTE_TYPE);

		FluidStorage.SIDED.registerForBlocks((world, pos, state, be, direction) -> CreativeFluidStorage.WATER, INFINITE_WATER_SOURCE);
		FluidStorage.SIDED.registerForBlocks((world, pos, state, be, direction) -> CreativeFluidStorage.LAVA, INFINITE_LAVA_SOURCE);
	}

	private static void registerBlock(Block block, String name) {
		Identifier id = new Identifier(MODID, name);
		Registry.register(Registry.BLOCK, id, block);
		Registry.register(Registry.ITEM, id, new BlockItem(block, new Item.Settings().group(ItemGroup.MISC)));
	}
}
