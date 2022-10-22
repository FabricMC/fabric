package net.fabricmc.fabric.api.itemgroup.v1;

import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;

/**
 * This interface is automatically implemented on all {@link ItemGroup} via Mixin and interface injection.
 */
public interface IdentifiableItemGroup {
	/**
	 * @return The unique identifier of this {@link ItemGroup}.
	 */
	Identifier getId();
}
