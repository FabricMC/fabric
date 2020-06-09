package net.fabricmc.fabric.mixin.content.registry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ShovelItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ShovelItem.class)
public interface ShovelItemAccessor {
	@Accessor(value = "PATH_BLOCKSTATES")
	Map<Block, BlockState> getPathBlockstates();
}
