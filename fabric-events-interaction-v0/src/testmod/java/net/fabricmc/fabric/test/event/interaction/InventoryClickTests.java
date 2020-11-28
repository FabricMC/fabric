/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.test.event.interaction;

import java.util.Optional;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.event.player.InventoryClickEvents;

@SuppressWarnings("deprecation")
public class InventoryClickTests implements ModInitializer, ClientModInitializer {
	public static final Item BIG_BUCKET = new FluidContainerItem(new Item.Settings().group(ItemGroup.MISC).maxCount(1), 1620 * 10);
	public static final Item TINY_BUCKET = new FluidContainerItem(new Item.Settings().group(ItemGroup.MISC).maxCount(1), 1620 / 3);

	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier("fabric-events-interaction-v0-testmod", "big_bucket"), BIG_BUCKET);
		Registry.register(Registry.ITEM, new Identifier("fabric-events-interaction-v0-testmod", "tiny_bucket"), TINY_BUCKET);
		InventoryClickEvents.CLICKED.register((itemStack, cursorStack, slot, screenHandler, clickType, player, playerInventory) -> {
			if (clickType == ClickType.RIGHT) {
				System.out.println("Right Clicked!");
			} else if (clickType == ClickType.LEFT) {
				System.out.println("Left Clicked!");
				return ActionResult.PASS;
			}

			// Here's some context
			// itemStack is the stack that was clicked. This one is in a slot in the inventory
			// cursorStack is the stack held by the cursor of the player. This one is not in a slot
			//
			// First, we check if itemStack is a FluidContainerItem (isFluidContainer)
			// If it is, we check if cursorStack contains fluid (containsFluid)
			// If both conditions match, we extract fluid from cursorStack and insert it into itemStack
			//
			// If itemStack isn't FluidContainerItem, we check if it is an empty container (isEmptyContainer)
			// If it is, we check if cursorStack is a FluidContainerItem
			// If both conditions match, we extract fluid from cursorStack and insert it to itemStack
			//
			// E.g.: cursorStack + itemStack -> outputs
			// water bucket + big bucket (6480 gb) -> bucket + big bucket (8100 gb)
			// big bucket (1620 gb) + glass bottle -> potion (water) + big bucket (1080 gb)
			// big bucket (1620 gb) + small bucket (0 gb) -> big bucket (1440 gb) + small bucket (180 gb)

			if (isFluidContainerItem(itemStack) && containsFluid(cursorStack)) {
				int fill = getFill(cursorStack);
				Ctx ctx = Ctx.fromStack(itemStack);

				if (isFluidContainerItem(cursorStack)) {
					Ctx newCtx = Ctx.fromStack(cursorStack);
					int insertable = newCtx.getMaxInsertable();

					if (fill > insertable) {
						ctx.drain(insertable).put(itemStack);
						newCtx.fill().put(cursorStack);
					} else {
						ctx.empty().put(itemStack);
						newCtx.insert(fill).put(cursorStack);
					}

					cursorStack.decrement(1);
					ItemStack newStack = getEmptyItemStack(cursorStack);
					insertOrSpawn(playerInventory, newStack);
				} else {
					if (ctx.canInsert(fill)) {
						ctx.insert(fill);
						cursorStack.decrement(1);
						insertOrSpawn(playerInventory, getEmptyItemStack(cursorStack));
					} else {
						return ActionResult.FAIL;
					}
				}
			} else if (isEmptyContainer(itemStack) && isFluidContainerItem(cursorStack)) {
				int am = getMaxCapacityOfEmpty(itemStack);
				Ctx ctx = Ctx.fromStack(cursorStack);

				if (ctx.canDrain(am)) {
					ctx.drain(am);
					cursorStack.decrement(1);
					insertOrSpawn(playerInventory, getEmptyItemStack(cursorStack));
				}
			}

			return ActionResult.PASS;
		});
	}

	private static boolean isInvalid(ItemStack stack) {
		Item i = stack.getItem();

		if (i instanceof FluidContainerItem) {
			return false;
		} else if (i == Items.GLASS_BOTTLE) {
			return false;
		} else if (i == Items.BUCKET) {
			return false;
		} else if (i == Items.WATER_BUCKET) {
			return false;
		} else if (i == Items.POTION)  {
			return PotionUtil.getPotion(stack) != Potions.WATER;
		}

		return true;
	}

	private static void insertOrSpawn(PlayerInventory inv, ItemStack stack) {
		if (!inv.insertStack(stack)) {
			ItemScatterer.spawn(inv.player.world, inv.player.getX(), inv.player.getY(), inv.player.getZ(), stack);
		}
	}

	private static ItemStack getEmptyItemStack(ItemStack in) {
		ItemStack result;

		if (in.isOf(Items.WATER_BUCKET)) {
			result = new ItemStack(Items.BUCKET);
		} else if (isValidPotion(in)) {
			result = new ItemStack(Items.GLASS_BOTTLE);
		} else {
			throw new IllegalArgumentException(String.format("Don't know how to convert item '%s' to its empty form", Registry.ITEM.getId(in.getItem())));
		}

		return result;
	}

	private static boolean isEmptyContainer(ItemStack stack){
		return stack.isOf(Items.GLASS_BOTTLE) || stack.isOf(Items.BUCKET);
	}

	private static boolean isFluidContainerItem(ItemStack stack) {
		return stack.getItem() instanceof FluidContainerItem;
	}

	private static boolean isValidPotion(ItemStack stack) {
		return stack.isOf(Items.POTION) && PotionUtil.getPotion(stack) != Potions.WATER;
	}

	private static boolean containsFluid(ItemStack stack) {
		return isFluidContainerItem(stack) || isValidPotion(stack) || stack.isOf(Items.WATER_BUCKET);
	}

	private static int getMaxCapacityOfEmpty(ItemStack stack) {
		if (stack.isOf(Items.GLASS_BOTTLE)) {
			return 540;
		} else if (stack.isOf(Items.BUCKET)) {
			return 1620;
		}

		return -1;
	}

	private static int getFill(ItemStack stack) {
		if (stack.isOf(Items.WATER_BUCKET)) {
			return 1620;
		} else if (stack.isOf(Items.POTION) && PotionUtil.getPotion(stack).equals(Potions.WATER)) {
			return 540;
		} else if (stack.isOf(BIG_BUCKET) || stack.isOf(TINY_BUCKET)) {
			return Ctx.fromStack(stack).getValue();
		}

		return 0;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void onInitializeClient() {
		ItemTooltipCallback.EVENT.register(((stack, context, lines) -> {
			if (isInvalid(stack)) {
				return;
			}

			if (!Screen.hasShiftDown() && !context.isAdvanced()) {
				lines.add(new LiteralText("Press shift for fluid info").formatted(Formatting.GOLD));
				return;
			}

			if (stack.isOf(Items.GLASS_BOTTLE)) {
				lines.add(new LiteralText("Fluid: 0 / 540 globules"));
			} else if (stack.isOf(Items.POTION) && PotionUtil.getPotion(stack).equals(Potions.WATER)) {
				lines.add(new LiteralText("Fluid: 540 / 540 globules").formatted(Formatting.DARK_BLUE));
			} else if (stack.isOf(Items.BUCKET)) {
				lines.add(new LiteralText("Fluid: 0 / 1620 globules"));
			} else if (stack.isOf(Items.WATER_BUCKET)) {
				lines.add(new LiteralText("Fluid: 1620 / 1620 globules").formatted(Formatting.DARK_BLUE));
			} else if (stack.isOf(BIG_BUCKET) || stack.isOf(TINY_BUCKET)) {
				Ctx ctx = Ctx.fromStack(stack);
				lines.add(new LiteralText(String.format("Fluid: %d / %d globules", ctx.getValue(), ctx.getMax())).formatted(this.getFormatting(ctx)));
			}
		}));
	}

	private Formatting getFormatting(Ctx ctx) {
		float f = ((float) ctx.getValue()) / ctx.getMax();

		if (f >= 1.0F) {
			return Formatting.DARK_BLUE;
		} else if (f > 0.7F) {
			return Formatting.BLUE;
		} else if (f > 0.4F) {
			return Formatting.DARK_AQUA;
		} else if (f > 0.1F) {
			return Formatting.AQUA;
		}

		return Formatting.WHITE;
	}

	public static final class Ctx {
		public static final Codec<Ctx> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.INT.fieldOf("value").forGetter(Ctx::getValue),
				Codec.INT.fieldOf("max").forGetter(Ctx::getMax)
		).apply(instance, Ctx::new));
		private final int value;
		private final int max;

		public Ctx(int value, int max) {
			Preconditions.checkArgument(value >= 0, "value must not be negative");
			Preconditions.checkArgument(max > 0, "max must be greater than zero");
			this.value = value;
			this.max = max;
		}

		public int getValue() {
			return this.value;
		}

		public int getMax() {
			return this.max;
		}

		public CompoundTag toTag() {
			return (CompoundTag) NbtOps.INSTANCE.withEncoder(CODEC).apply(this).getOrThrow(false, System.err::println);
		}

		public boolean canDrain(int amount) {
			return this.value >= amount;
		}

		public boolean canInsert(int amount) {
			return this.max >= (this.value + amount);
		}

		public Ctx empty() {
			return new Ctx(0, this.max);
		}

		public Ctx fill() {
			return new Ctx(this.max, this.max);
		}

		public Ctx drain(int amount) {
			return new Ctx(this.max, this.value - amount);
		}

		public Ctx insert(int amount) {
			return new Ctx(this.max, this.value + amount);
		}

		public int getMaxInsertable() {
			return this.max - this.value;
		}

		public void put(ItemStack stack) {
			stack.getOrCreateTag().put("Fluid", this.toTag());
		}

		public static Ctx fromTag(CompoundTag tag) {
			return NbtOps.INSTANCE.withParser(CODEC).apply(tag).getOrThrow(false, System.err::println);
		}

		public static Ctx fromStack(ItemStack stack) {
			return fromTag(Optional.ofNullable(stack.getSubTag("Fluid")).orElse(new Ctx(0, 1620).toTag()));
		}
	}
}
