package net.fabricmc.fabric.impl.tool.attribute.handlers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.tag.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;

import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;
import net.fabricmc.fabric.impl.tool.attribute.ToolManagerImpl;

/**
 * This handler handles items that are registered in a tool tag,
 * but aren't any known tool items in code. For that reason, we use a few callback values:
 * The mining level of this kind of item is always 0, and the mining speed multiplier for matching
 * blocks is {@link TaggedToolsModdedBlocksToolHandler#GENERIC_FASTER_MINING_SPEED}.
 *
 * <p>Only applicable to items that are not a subclass of {@link DynamicAttributeTool} or {@link ToolItem}</p>
 */
public class TaggedToolsTaggedBlocksToolHandler implements ToolManagerImpl.ToolHandler {
	private final Tag<Block> mineableTag;

	public TaggedToolsTaggedBlocksToolHandler(Tag<Block> mineableTag) {
		this.mineableTag = mineableTag;
	}

	@NotNull
	@Override
	public ActionResult isEffectiveOn(Tag<Item> tag, BlockState state, ItemStack stack, @Nullable LivingEntity user) {
		if (!(stack.getItem() instanceof DynamicAttributeTool) && !(stack.getItem() instanceof ToolItem)) {
			if (state.isIn(mineableTag)) {
				return ActionResult.SUCCESS;
			}
		}

		return ActionResult.PASS;
	}

	@NotNull
	@Override
	public TypedActionResult<Float> getMiningSpeedMultiplier(Tag<Item> tag, BlockState state, ItemStack stack, @Nullable LivingEntity user) {
		if (!(stack.getItem() instanceof DynamicAttributeTool) && !(stack.getItem() instanceof ToolItem)) {
			if (state.isIn(mineableTag)) {
				return TypedActionResult.success(TaggedToolsModdedBlocksToolHandler.GENERIC_FASTER_MINING_SPEED);
			}
		}

		return TypedActionResult.pass(1.0F);
	}
}
