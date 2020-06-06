package net.fabricmc.fabric.test.transaction;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.fabricmc.api.ModInitializer;

public class TransactionTest implements ModInitializer {
	public static final String MODID = "fabric-transaction-api-v1-testmod";

	@Override
	public void onInitialize() {
		TransferBlock block = Registry.register(Registry.BLOCK, new Identifier(MODID, "transfer_block"), new TransferBlock(AbstractBlock.Settings.of(Material.STONE)));
		Registry.register(Registry.ITEM, new Identifier(MODID, "transfer_block"), new BlockItem(block, new Item.Settings().group(ItemGroup.MISC)));
	}
}
