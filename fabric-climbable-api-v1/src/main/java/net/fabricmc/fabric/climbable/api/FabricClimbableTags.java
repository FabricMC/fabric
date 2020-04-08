package net.fabricmc.fabric.climbable.api;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class FabricClimbableTags {

	public static final Tag<Block> CLIMBABLE = TagRegistry.block(new Identifier("minecraft", "climbable"));

	private FabricClimbableTags() {	}
}
