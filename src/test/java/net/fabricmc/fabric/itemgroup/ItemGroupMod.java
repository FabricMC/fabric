package net.fabricmc.fabric.itemgroup;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemGroupMod implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		new FabricItemGroup(new Identifier("fabric", "test1")).icon(() -> new ItemStack(Items.APPLE)).create();
		new FabricItemGroup(new Identifier("fabric", "test2")).create();
		new FabricItemGroup(new Identifier("fabric", "test3")).create();
		new FabricItemGroup(new Identifier("fabric", "test4")).create();
		new FabricItemGroup(new Identifier("fabric", "test5")).create();
		new FabricItemGroup(new Identifier("fabric", "test6")).create();
		new FabricItemGroup(new Identifier("fabric", "test7")).create();
		new FabricItemGroup(new Identifier("fabric", "test8")).create();
		new FabricItemGroup(new Identifier("fabric", "test9")).create();
		new FabricItemGroup(new Identifier("fabric", "test10")).create();
		new FabricItemGroup(new Identifier("fabric", "test11")).create();
		new FabricItemGroup(new Identifier("fabric", "test12")).create();
		new FabricItemGroup(new Identifier("fabric", "test13")).create();
		ItemGroup group = new FabricItemGroup(new Identifier("fabric", "test14")).create();

		Item testItem = new Item(new Item.Settings().itemGroup(group));
		Registry.ITEM.register(new Identifier("fabric_test", "itemgroup"), testItem);
	}
}
