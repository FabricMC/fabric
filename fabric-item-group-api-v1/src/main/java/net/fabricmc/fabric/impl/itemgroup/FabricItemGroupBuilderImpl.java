package net.fabricmc.fabric.impl.itemgroup;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;

@ApiStatus.Internal
public final class FabricItemGroupBuilderImpl extends ItemGroup.Builder {
	public FabricItemGroupBuilderImpl(Identifier identifier) {
		// Set when building.
		super(null, -1);
	}

	@Override
	public ItemGroup build() {
		// TODO set top/botoom and ids here
		final ItemGroup itemGroup = super.build();
		ItemGroupHelper.appendItemGroup(itemGroup);
		return itemGroup;
	}
}
