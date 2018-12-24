package net.fabricmc.fabric.api.client.itemgroup;

import net.fabricmc.fabric.client.itemgroup.ItemGroupExtensions;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public final class FabricItemGroup {

	Identifier identifier;
	Supplier<ItemStack> stackSupplier = () -> ItemStack.EMPTY;

	public FabricItemGroup(Identifier identifier) {
		this.identifier = identifier;
	}

	public FabricItemGroup icon(Supplier<ItemStack> stackSupplier){
		this.stackSupplier = stackSupplier;
		return this;
	}

	public ItemGroup create(){
		((ItemGroupExtensions)ItemGroup.BUILDING_BLOCKS).fabric_expandArray();
		return new ItemGroup(ItemGroup.GROUPS.length -1, identifier.toString()) {
			@Override
			public ItemStack getIconItem() {
				return stackSupplier.get();
			}
		};
	}


}
