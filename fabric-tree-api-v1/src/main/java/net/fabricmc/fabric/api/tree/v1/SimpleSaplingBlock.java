package net.fabricmc.fabric.api.tree.v1;

import net.minecraft.block.SaplingBlock;
import net.minecraft.block.sapling.SaplingGenerator;

/**
 * <p>A {@link SaplingBlock} with a public constructor to create your own saplings.</p>
 *
 * <p>This could be done using an access widener on the {@link SaplingBlock} constructor, but access wideners should be avoided where possible.</p>
 *
 * <p>
 *     An example of using this implementation:<br>
 *
 *     <code>
 *			public static final SimpleSaplingBlock MY_SAPLING = new SimpleSaplingBlock(new SimpleSaplingGenerator(MY_TREE_CONFIGURED), FabricBlockSettings.copyOf(Blocks.OAK_SAPLING));
 *     </code>
 * </p>
 */
public class SimpleSaplingBlock extends SaplingBlock {
	public SimpleSaplingBlock(SaplingGenerator generator, Settings settings) {
		super(generator, settings);
	}
}
