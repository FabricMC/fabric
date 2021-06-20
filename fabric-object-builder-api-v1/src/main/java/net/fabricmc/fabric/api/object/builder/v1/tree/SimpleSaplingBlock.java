package net.fabricmc.fabric.api.object.builder.v1.tree;

import net.minecraft.block.SaplingBlock;
import net.minecraft.block.sapling.SaplingGenerator;

/**
 * A temporary solution because access wideners cannot be applied to dependents (yet).<br>
 * Allows you to create your own {@link SaplingBlock}s.
 */
public class SimpleSaplingBlock extends SaplingBlock {
	protected SimpleSaplingBlock(SaplingGenerator generator, Settings settings) {
		super(generator, settings);
	}
}
