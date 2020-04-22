package net.fabricmc.fabric.impl.tool.attribute;

import com.google.common.collect.ImmutableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShearsItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Lazy;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.fabricmc.fabric.api.tool.attribute.v1.ToolManager;
import net.fabricmc.fabric.mixin.tool.attribute.MiningToolItemAccessor;
import net.fabricmc.fabric.mixin.tool.attribute.ToolItemAccessor;

public class ToolHandlers implements ModInitializer {
	private static final Logger LOGGER = LogManager.getLogger("fabric-tool-attribute-api-v1");

	@Override
	public void onInitialize() {
		ToolManager.general().register(new ModdedToolsModdedBlocksToolHandler());
		ToolManager.general().register(new VanillaToolsModdedBlocksToolHandler());
		ToolManager.general().register(new VanillaToolsVanillaBlocksToolHandler());
		ToolManager.tag(FabricToolTags.PICKAXES).register(new ModdedMiningToolsVanillaBlocksToolHandler(
				ImmutableList.of(
						new Lazy<>(() -> createPickaxe(ToolMaterials.WOOD)),
						new Lazy<>(() -> createPickaxe(ToolMaterials.STONE)),
						new Lazy<>(() -> createPickaxe(ToolMaterials.IRON)),
						new Lazy<>(() -> createPickaxe(ToolMaterials.DIAMOND))
				)
		));
		ToolManager.tag(FabricToolTags.AXES).register(new ModdedMiningToolsVanillaBlocksToolHandler(
				ImmutableList.of(
						new Lazy<>(() -> createAxe(ToolMaterials.WOOD)),
						new Lazy<>(() -> createAxe(ToolMaterials.STONE)),
						new Lazy<>(() -> createAxe(ToolMaterials.IRON)),
						new Lazy<>(() -> createAxe(ToolMaterials.DIAMOND))
				)
		));
		ToolManager.tag(FabricToolTags.SHOVELS).register(new ModdedMiningToolsVanillaBlocksToolHandler(
				ImmutableList.of(
						new Lazy<>(() -> createShovel(ToolMaterials.WOOD)),
						new Lazy<>(() -> createShovel(ToolMaterials.STONE)),
						new Lazy<>(() -> createShovel(ToolMaterials.IRON)),
						new Lazy<>(() -> createShovel(ToolMaterials.DIAMOND))
				)
		));
		ToolManager.tag(FabricToolTags.HOES).register(new ModdedMiningToolsVanillaBlocksToolHandler(
				ImmutableList.of(
						new Lazy<>(() -> createHoe(ToolMaterials.WOOD)),
						new Lazy<>(() -> createHoe(ToolMaterials.STONE)),
						new Lazy<>(() -> createHoe(ToolMaterials.IRON)),
						new Lazy<>(() -> createHoe(ToolMaterials.DIAMOND))
				)
		));
		ToolManager.tag(FabricToolTags.SWORDS).register(new ModdedMiningToolsVanillaBlocksToolHandler(
				ImmutableList.of(
						new Lazy<>(() -> createSword(ToolMaterials.WOOD)),
						new Lazy<>(() -> createSword(ToolMaterials.STONE)),
						new Lazy<>(() -> createSword(ToolMaterials.IRON)),
						new Lazy<>(() -> createSword(ToolMaterials.DIAMOND))
				)
		));
		ToolManager.tag(FabricToolTags.SHEARS).register(new ShearsVanillaBlocksToolHandler());
	}

	private Item createPickaxe(ToolMaterial material) {
		return new DummyPickaxeItem(material, 1, -2.8F, new Item.Settings());
	}

	private Item createAxe(ToolMaterial material) {
		return new DummyAxeItem(material, 6.0F, -3.2F, new Item.Settings());
	}

	private Item createShovel(ToolMaterial material) {
		return new ShovelItem(material, 1.5F, -3.0F, new Item.Settings());
	}

	private Item createHoe(ToolMaterial material) {
		return new HoeItem(material, -2.0F, new Item.Settings());
	}

	private Item createSword(ToolMaterial material) {
		return new SwordItem(material, 3, -2.4F, new Item.Settings());
	}

	private static class DummyPickaxeItem extends PickaxeItem {
		private DummyPickaxeItem(ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
			super(material, attackDamage, attackSpeed, settings);
		}
	}

	private static class DummyAxeItem extends AxeItem {
		public DummyAxeItem(ToolMaterial material, float attackDamage, float attackSpeed, Settings settings) {
			super(material, attackDamage, attackSpeed, settings);
		}
	}

	/**
	 * This handler handles items that are an subclass of {@link DynamicAttributeTool} by getting their mining level
	 * from {@link DynamicAttributeTool#getMiningLevel(Tag, BlockState, ItemStack, LivingEntity)} and comparing to the block mining level.
	 *
	 * <p>Only applicable to modded blocks that are registered, as only they have the registered required mining level.</p>
	 */
	private static class ModdedToolsModdedBlocksToolHandler implements ToolManager.ToolHandler {
		@Override
		public ActionResult isEffectiveOn(Tag<Item> tag, ItemStack stack, LivingEntity user, BlockState state) {
			if (stack.getItem() instanceof DynamicAttributeTool) {
				ToolManagerImpl.Entry entry = ToolManagerImpl.entryNullable(state.getBlock());

				if (entry != null) {
					int miningLevel = ((DynamicAttributeTool) stack.getItem()).getMiningLevel(tag, state, stack, user);

					if (miningLevel >= entry.getMiningLevel(tag)) {
						return ActionResult.SUCCESS;
					}
				}
			}

			return ActionResult.PASS;
		}
	}

	/**
	 * This handler handles items that are not a subclass of {@link DynamicAttributeTool} by
	 * getting their mining level by {@link ToolMaterial#getMiningLevel()} and comparing to the block mining level.
	 *
	 * <p>Only applicable to modded blocks that are registered, as only they have the registered required mining level.</p>
	 */
	private static class VanillaToolsModdedBlocksToolHandler implements ToolManager.ToolHandler {
		@Override
		public ActionResult isEffectiveOn(Tag<Item> tag, ItemStack stack, LivingEntity user, BlockState state) {
			if (!(stack.getItem() instanceof DynamicAttributeTool)) {
				ToolManagerImpl.Entry entry = ToolManagerImpl.entryNullable(state.getBlock());

				if (entry != null) {
					int miningLevel = stack.getItem() instanceof ToolItem ? ((ToolItem) stack.getItem()).getMaterial().getMiningLevel() : 0;

					if (miningLevel >= entry.getMiningLevel(tag)) {
						return ActionResult.SUCCESS;
					}
				}
			}

			return ActionResult.PASS;
		}
	}

	/**
	 * This handler handles items that are not a subclass of {@link DynamicAttributeTool} by
	 * using the vanilla {@link Item#isEffectiveOn(BlockState)}.
	 *
	 * <p>Only applicable to blocks that are vanilla or share the material that is handled by their vanilla tool.</p>
	 */
	private static class VanillaToolsVanillaBlocksToolHandler implements ToolManager.ToolHandler {
		@Override
		public ActionResult isEffectiveOn(Tag<Item> tag, ItemStack stack, LivingEntity user, BlockState state) {
			return !(stack.getItem() instanceof DynamicAttributeTool) && stack.getItem().isEffectiveOn(state) ? ActionResult.SUCCESS : ActionResult.PASS;
		}

		@Override
		public Float getMiningSpeedMultiplier(Tag<Item> tag, ItemStack stack, LivingEntity user, BlockState state) {
			if (!(stack.getItem() instanceof DynamicAttributeTool)) {
				float miningSpeed = stack.getItem().getMiningSpeed(stack, state);

				if (miningSpeed != 1f) {
					return miningSpeed;
				}
			}

			return null;
		}
	}

	/**
	 * This handler handles items that are a subclass of {@link DynamicAttributeTool} or not a subclass of {@link ToolItem} by
	 * using the vanilla {@link Item#isEffectiveOn(BlockState)} by using a custom fake tool material to use the mining level
	 * from {@link DynamicAttributeTool#getMiningLevel(Tag, BlockState, ItemStack, LivingEntity)} or by default {@code 0}
	 * if the item is not a subclass of {@link DynamicAttributeTool}.
	 *
	 * <p>Only applicable to blocks that are vanilla or share the material that is handled by their vanilla tool.</p>
	 */
	private static class ModdedMiningToolsVanillaBlocksToolHandler implements ToolManager.ToolHandler {
		private final FakeAdaptableToolMaterial fakeMaterial = new FakeAdaptableToolMaterial();
		private final ImmutableList<Lazy<Item>> vanillaItems;

		private ModdedMiningToolsVanillaBlocksToolHandler(ImmutableList<Lazy<Item>> vanillaItems) {
			this.vanillaItems = vanillaItems;
		}

		private Item getVanillaItem(int miningLevel) {
			if (miningLevel < 0) return vanillaItems.get(0).get();
			if (miningLevel >= vanillaItems.size()) return vanillaItems.get(vanillaItems.size() - 1).get();
			return vanillaItems.get(miningLevel).get();
		}

		@Override
		public ActionResult isEffectiveOn(Tag<Item> tag, ItemStack stack, LivingEntity user, BlockState state) {
			if (stack.getItem() instanceof DynamicAttributeTool) {
				int miningLevel = ((DynamicAttributeTool) stack.getItem()).getMiningLevel(tag, state, stack, user);
				if (miningLevel < 0) return ActionResult.PASS;
				Item vanillaItem = getVanillaItem(miningLevel);
				ToolMaterial tempMaterial = ((ToolItem) vanillaItem).getMaterial();
				fakeMaterial.miningLevel = miningLevel;
				((ToolItemAccessor) vanillaItem).setMaterial(fakeMaterial);
				boolean effective = vanillaItem.isEffectiveOn(state);
				((ToolItemAccessor) vanillaItem).setMaterial(tempMaterial);
				return effective ? ActionResult.SUCCESS : ActionResult.PASS;
			}

			return ActionResult.PASS;
		}

		@Override
		public Float getMiningSpeedMultiplier(Tag<Item> tag, ItemStack stack, LivingEntity user, BlockState state) {
			if (stack.getItem() instanceof DynamicAttributeTool) {
				int miningLevel = ((DynamicAttributeTool) stack.getItem()).getMiningLevel(tag, state, stack, user);
				if (miningLevel < 0) return null;
				Item vanillaItem = getVanillaItem(miningLevel);
				float tempMiningSpeed = ((MiningToolItemAccessor) vanillaItem).getMiningSpeed();
				((MiningToolItemAccessor) vanillaItem).setMiningSpeed(((DynamicAttributeTool) stack.getItem()).getMiningSpeedMultiplier(tag, state, stack, user));
				float miningSpeed = vanillaItem.getMiningSpeed(stack, state);
				((MiningToolItemAccessor) vanillaItem).setMiningSpeed(tempMiningSpeed);
				return miningSpeed != 1f ? miningSpeed : null;
			}

			return null;
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
	private static class ShearsVanillaBlocksToolHandler implements ToolManager.ToolHandler {
		private final Item vanillaItem = Items.SHEARS;

		@Override
		public ActionResult isEffectiveOn(Tag<Item> tag, ItemStack stack, LivingEntity user, BlockState state) {
			if (!(stack.getItem() instanceof DynamicAttributeTool)) {
				if (!(stack.getItem() instanceof ShearsItem)) {
					return vanillaItem.isEffectiveOn(state) ? ActionResult.SUCCESS : ActionResult.PASS;
				} else {
					return stack.getItem().isEffectiveOn(state) ? ActionResult.SUCCESS : ActionResult.PASS;
				}
			}

			return ActionResult.PASS;
		}

		@Override
		public Float getMiningSpeedMultiplier(Tag<Item> tag, ItemStack stack, LivingEntity user, BlockState state) {
			Float speed = null;

			if (!(stack.getItem() instanceof DynamicAttributeTool)) {
				if (!(stack.getItem() instanceof ShearsItem)) {
					speed = vanillaItem.getMiningSpeed(new ItemStack(vanillaItem), state);
				} else {
					speed = stack.getItem().getMiningSpeed(stack, state);
				}
			}

			return speed != 1f ? speed : null;
		}
	}

	private static class FakeAdaptableToolMaterial implements ToolMaterial {
		private int miningLevel = 0;

		@Override
		public int getDurability() {
			return 0;
		}

		@Override
		public float getMiningSpeed() {
			return 0;
		}

		@Override
		public float getAttackDamage() {
			return 0;
		}

		@Override
		public int getMiningLevel() {
			return miningLevel;
		}

		@Override
		public int getEnchantability() {
			return 0;
		}

		@Override
		public Ingredient getRepairIngredient() {
			return Ingredient.EMPTY;
		}
	}
}
