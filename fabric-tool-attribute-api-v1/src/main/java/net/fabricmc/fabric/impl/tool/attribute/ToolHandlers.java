package net.fabricmc.fabric.impl.tool.attribute;

import java.util.Arrays;
import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ShearsItem;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.tag.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.fabricmc.fabric.mixin.tool.attribute.MiningToolItemAccessor;

/**
 * Entrypoint to register the default tool handlers.
 */
public class ToolHandlers implements ModInitializer {
	@Override
	public void onInitialize() {
		ToolManagerImpl.general().register(new ModdedToolsModdedBlocksToolHandler());
		ToolManagerImpl.general().register(new VanillaToolsModdedBlocksToolHandler());
		ToolManagerImpl.general().register(new VanillaToolsVanillaBlocksToolHandler());
		ToolManagerImpl.tag(FabricToolTags.PICKAXES).register(new ModdedMiningToolsVanillaBlocksToolHandler(
				Arrays.asList(
						Items.WOODEN_PICKAXE,
						Items.STONE_PICKAXE,
						Items.IRON_PICKAXE,
						Items.DIAMOND_PICKAXE
				)
		));
		ToolManagerImpl.tag(FabricToolTags.AXES).register(new ModdedMiningToolsVanillaBlocksToolHandler(
				Arrays.asList(
						Items.WOODEN_AXE,
						Items.STONE_AXE,
						Items.IRON_AXE,
						Items.DIAMOND_AXE
				)
		));
		ToolManagerImpl.tag(FabricToolTags.SHOVELS).register(new ModdedMiningToolsVanillaBlocksToolHandler(
				Arrays.asList(
						Items.WOODEN_SHOVEL,
						Items.STONE_SHOVEL,
						Items.IRON_SHOVEL,
						Items.DIAMOND_SHOVEL
				)
		));
		ToolManagerImpl.tag(FabricToolTags.HOES).register(new ModdedMiningToolsVanillaBlocksToolHandler(
				Arrays.asList(
						Items.WOODEN_HOE,
						Items.STONE_HOE,
						Items.IRON_HOE,
						Items.DIAMOND_HOE
				)
		));
		ToolManagerImpl.tag(FabricToolTags.SWORDS).register(new ModdedMiningToolsVanillaBlocksToolHandler(
				Arrays.asList(
						Items.WOODEN_SWORD,
						Items.STONE_SWORD,
						Items.IRON_SWORD,
						Items.DIAMOND_SWORD
				)
		));
		ToolManagerImpl.tag(FabricToolTags.SHEARS).register(new ShearsVanillaBlocksToolHandler());
	}

	/**
	 * This handler handles items that are an subclass of {@link DynamicAttributeTool} by comparing their mining level
	 * using {@link DynamicAttributeTool#getMiningLevel(Tag, BlockState, ItemStack, LivingEntity)} and the block mining level.
	 *
	 * <p>Only applicable to modded blocks that are registered, as only they have the registered required mining level.</p>
	 */
	private static class ModdedToolsModdedBlocksToolHandler implements ToolManagerImpl.ToolHandler {
		@Override
		public ActionResult isEffectiveOn(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
			if (stack.getItem() instanceof DynamicAttributeTool) {
				ToolManagerImpl.Entry entry = ToolManagerImpl.entryNullable(state.getBlock());

				if (entry != null) {
					int miningLevel = ((DynamicAttributeTool) stack.getItem()).getMiningLevel(tag, state, stack, user);
					int requiredMiningLevel = entry.getMiningLevel(tag);

					return requiredMiningLevel >= 0 && miningLevel >= 0 && miningLevel >= requiredMiningLevel ? ActionResult.SUCCESS : ActionResult.PASS;
				}
			}

			return ActionResult.PASS;
		}

		@Override
		public TypedActionResult<Float> getMiningSpeedMultiplier(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
			if (stack.getItem() instanceof DynamicAttributeTool) {
				float multiplier = ((DynamicAttributeTool) stack.getItem()).getMiningSpeedMultiplier(tag, state, stack, user);
				if (multiplier != 1f) return TypedActionResult.success(multiplier);
			}

			return TypedActionResult.pass(1f);
		}
	}

	/**
	 * This handler handles items that are not a subclass of {@link DynamicAttributeTool} by
	 * comparing their mining level using {@link ToolMaterial#getMiningLevel()} and the block mining level.
	 *
	 * <p>Only applicable to modded blocks that are registered, as only they have the registered required mining level.</p>
	 */
	private static class VanillaToolsModdedBlocksToolHandler implements ToolManagerImpl.ToolHandler {
		@Override
		public ActionResult isEffectiveOn(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
			if (!(stack.getItem() instanceof DynamicAttributeTool)) {
				ToolManagerImpl.Entry entry = ToolManagerImpl.entryNullable(state.getBlock());

				if (entry != null) {
					int miningLevel = stack.getItem() instanceof ToolItem ? ((ToolItem) stack.getItem()).getMaterial().getMiningLevel() : -1;
					int requiredMiningLevel = entry.getMiningLevel(tag);
					return requiredMiningLevel >= 0 && miningLevel >= 0 && miningLevel >= requiredMiningLevel ? ActionResult.SUCCESS : ActionResult.PASS;
				}
			}

			return ActionResult.PASS;
		}

		@Override
		public TypedActionResult<Float> getMiningSpeedMultiplier(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
			if (!(stack.getItem() instanceof DynamicAttributeTool)) {
				float multiplier = stack.getItem() instanceof MiningToolItem ? ((MiningToolItemAccessor) stack.getItem()).getMiningSpeed() : stack.getItem().getMiningSpeed(stack, state);
				if (multiplier != 1f) return TypedActionResult.success(multiplier);
			}

			return TypedActionResult.pass(1f);
		}
	}

	/**
	 * This handler handles items that are not a subclass of {@link DynamicAttributeTool} by
	 * using the vanilla {@link Item#isEffectiveOn(BlockState)}.
	 *
	 * <p>Only applicable to blocks that are vanilla or share the material that is handled by their vanilla tool.</p>
	 */
	private static class VanillaToolsVanillaBlocksToolHandler implements ToolManagerImpl.ToolHandler {
		@Override
		public ActionResult isEffectiveOn(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
			if (!(stack.getItem() instanceof DynamicAttributeTool)) {
				ToolManagerImpl.Entry entry = ToolManagerImpl.entryNullable(state.getBlock());

				if (entry == null) {
					return stack.getItem().isEffectiveOn(state) || stack.getItem().getMiningSpeed(stack, state) != 1f ? ActionResult.SUCCESS : ActionResult.PASS;
				}
			}

			return ActionResult.PASS;
		}

		@Override
		public TypedActionResult<Float> getMiningSpeedMultiplier(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
			if (!(stack.getItem() instanceof DynamicAttributeTool)) {
				float miningSpeed = stack.getItem().getMiningSpeed(stack, state);

				if (miningSpeed != 1f) {
					return TypedActionResult.success(miningSpeed);
				}
			}

			return TypedActionResult.pass(1f);
		}
	}

	/**
	 * This handler handles items that are a subclass of {@link DynamicAttributeTool} by using the
	 * vanilla {@link Item#isEffectiveOn(BlockState)} with a custom fake tool material to use the mining level
	 * from {@link DynamicAttributeTool#getMiningLevel(Tag, BlockState, ItemStack, LivingEntity)}.
	 *
	 * <p>Only applicable to blocks that are vanilla or share the material that is handled by their vanilla tool.</p>
	 */
	private static class ModdedMiningToolsVanillaBlocksToolHandler implements ToolManagerImpl.ToolHandler {
		private final List<Item> vanillaItems;

		private ModdedMiningToolsVanillaBlocksToolHandler(List<Item> vanillaItems) {
			this.vanillaItems = vanillaItems;
		}

		private ToolItem getVanillaItem(int miningLevel) {
			if (miningLevel < 0) return (ToolItem) vanillaItems.get(0);
			if (miningLevel >= vanillaItems.size()) return (ToolItem) vanillaItems.get(vanillaItems.size() - 1);
			return (ToolItem) vanillaItems.get(miningLevel);
		}

		@Override
		public ActionResult isEffectiveOn(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
			if (stack.getItem() instanceof DynamicAttributeTool) {
				if (ToolManagerImpl.entryNullable(state.getBlock()) != null) {
					// Block is a modded block, and we should ignore it
					return ActionResult.PASS;
				}

				// Gets the mining level from our modded tool
				int miningLevel = ((DynamicAttributeTool) stack.getItem()).getMiningLevel(tag, state, stack, user);
				if (miningLevel < 0) return ActionResult.PASS;

				ToolItem vanillaItem = getVanillaItem(miningLevel);
				boolean effective = vanillaItem.isEffectiveOn(state) || vanillaItem.getMiningSpeed(new ItemStack(vanillaItem), state) != 1f;
				return effective ? ActionResult.SUCCESS : ActionResult.PASS;
			}

			return ActionResult.PASS;
		}

		@Override
		public TypedActionResult<Float> getMiningSpeedMultiplier(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
			if (stack.getItem() instanceof DynamicAttributeTool) {
				// Gets the mining level from our modded tool
				int miningLevel = ((DynamicAttributeTool) stack.getItem()).getMiningLevel(tag, state, stack, user);
				if (miningLevel < 0) return null;

				float moddedToolSpeed = ((DynamicAttributeTool) stack.getItem()).getMiningSpeedMultiplier(tag, state, stack, user);
				ToolItem firstVanillaItem = getVanillaItem(miningLevel);
				ToolItem secondVanillaItem = getVanillaItem(miningLevel + 1 >= vanillaItems.size() ? vanillaItems.size() - 2 : miningLevel + 1);

				float firstSpeed = firstVanillaItem.getMiningSpeed(new ItemStack(firstVanillaItem, 1), state);
				float secondSpeed = secondVanillaItem.getMiningSpeed(new ItemStack(secondVanillaItem, 1), state);
				boolean hasForcedSpeed = firstSpeed == secondSpeed && firstSpeed >= 1f;

				// Has forced speed, which as actions like swords breaking cobwebs.
				if (hasForcedSpeed) {
					return secondSpeed != 1f ? TypedActionResult.success(secondSpeed) : TypedActionResult.pass(1f);
				}

				// We adjust the mining speed according to the ratio for the closest tool.
				float adjustedMiningSpeed = firstSpeed / firstVanillaItem.getMaterial().getMiningSpeed() * moddedToolSpeed;
				return adjustedMiningSpeed != 1f ? TypedActionResult.success(adjustedMiningSpeed) : TypedActionResult.pass(1f);
			}

			return TypedActionResult.pass(1f);
		}
	}

	/**
	 * This handler handles items that are registered in the {@link FabricToolTags#SHEARS} by using the
	 * vanilla {@link Item#isEffectiveOn(BlockState)} using the vanilla shears or the item itself if the item
	 * is a subclass of {@link ShearsItem}.
	 *
	 * <p>Only applicable to items that are not a subclass of {@link DynamicAttributeTool}</p>
	 * <p>Only applicable to blocks that are vanilla or share the material that is handled by their vanilla tool.</p>
	 */
	private static class ShearsVanillaBlocksToolHandler implements ToolManagerImpl.ToolHandler {
		private final Item vanillaItem = Items.SHEARS;

		@Override
		public ActionResult isEffectiveOn(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
			if (ToolManagerImpl.entryNullable(state.getBlock()) != null) {
				// Block is a modded block, and we should ignore it
				return ActionResult.PASS;
			}

			if (!(stack.getItem() instanceof DynamicAttributeTool)) {
				if (!(stack.getItem() instanceof ShearsItem)) {
					return vanillaItem.isEffectiveOn(state) || vanillaItem.getMiningSpeed(new ItemStack(vanillaItem), state) != 1f ? ActionResult.SUCCESS : ActionResult.PASS;
				} else {
					return stack.getItem().isEffectiveOn(state) || stack.getItem().getMiningSpeed(stack, state) != 1f ? ActionResult.SUCCESS : ActionResult.PASS;
				}
			}

			return ActionResult.PASS;
		}

		@Override
		public TypedActionResult<Float> getMiningSpeedMultiplier(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
			float speed = 1f;

			if (!(stack.getItem() instanceof DynamicAttributeTool)) {
				if (!(stack.getItem() instanceof ShearsItem)) {
					speed = vanillaItem.getMiningSpeed(new ItemStack(vanillaItem), state);
				} else {
					speed = stack.getItem().getMiningSpeed(stack, state);
				}
			}

			return speed != 1f ? TypedActionResult.success(speed) : TypedActionResult.pass(1f);
		}
	}
}
