package net.fabricmc.fabric.test.item.group;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;

public class ItemGroupTest implements ModInitializer {
	//Adds an item group with all items in it
	private static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.create(new Identifier("fabric-item-groups-v0-testmod", "test_group"))
				.icon(() -> new ItemStack(Items.DIAMOND))
				.appendItems(stacks ->
						Registry.ITEM.stream()
						.map(ItemStack::new)
						.forEach(stacks::add)
				).build();

	@Override
	public void onInitialize() {
	}
}
