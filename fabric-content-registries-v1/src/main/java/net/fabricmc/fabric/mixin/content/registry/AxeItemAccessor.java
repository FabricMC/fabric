package net.fabricmc.fabric.mixin.content.registry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ShovelItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(AxeItem.class)
public interface AxeItemAccessor {
	@Accessor(value = "STRIPPED_BLOCKS")
	Map<Block, BlockState> getStrippedBlocks();
}
