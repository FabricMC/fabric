package net.fabricmc.fabric.mixin.itemgroup;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.itemgroup.v1.IdentifiableItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.MinecraftItemGroups;

@Mixin(ItemGroup.class)
public class ItemGroupMixin implements IdentifiableItemGroup {
	@Override
	public Identifier getId() {
		final Identifier identifier = MinecraftItemGroups.MAP.get((ItemGroup) (Object) this);

		if (identifier == null) {
			throw new NullPointerException("Unable to find identifier for " + this.getClass());
		}

		return identifier;
	}
}
