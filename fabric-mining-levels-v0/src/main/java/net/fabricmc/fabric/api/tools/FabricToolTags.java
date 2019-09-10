package net.fabricmc.fabric.api.tools;

import net.minecraft.item.Item;
import net.minecraft.tag.Tag;

/**
 * @deprecated Use the v1 class, at {@link net.fabricmc.fabric.api.tools.v1.FabricToolTags}
 */
@Deprecated
public class FabricToolTags {
	public static final Tag<Item> AXES = net.fabricmc.fabric.api.tools.v1.FabricToolTags.AXES;
	public static final Tag<Item> HOES = net.fabricmc.fabric.api.tools.v1.FabricToolTags.HOES;
	public static final Tag<Item> PICKAXES = net.fabricmc.fabric.api.tools.v1.FabricToolTags.PICKAXES;
	public static final Tag<Item> SHOVELS = net.fabricmc.fabric.api.tools.v1.FabricToolTags.SHOVELS;
	public static final Tag<Item> SWORDS = net.fabricmc.fabric.api.tools.v1.FabricToolTags.SWORDS;
}
