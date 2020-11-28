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
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.tag.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.event.player.InventoryClickEvents;
import net.fabricmc.fabric.api.tag.TagRegistry;

@SuppressWarnings("deprecation")
public class InventoryClickTests implements ModInitializer, ClientModInitializer {
	public static final Tag<Item> CLICKABLES = TagRegistry.item(new Identifier("fabric-events-interaction-v0-testmod", "clickables"));
	public static final Item BIG_BUCKET = new Item(new Item.Settings().group(ItemGroup.MISC).maxCount(1)) {
		@Override
		public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
			if (!stack.getOrCreateTag().contains("Fluid")) {
				stack.putSubTag("Fluid", new Ctx(0, 1620 * 10).toTag());
			}
		}

		@Override
		public boolean isItemBarVisible(ItemStack stack) {
			return true;
		}

		@Override
		public boolean hasGlint(ItemStack stack) {
			return true;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public int getItemBarColor(ItemStack stack) {
			return 0x0044FF;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public int getItemBarStep(ItemStack stack) {
			CompoundTag fluidTag =  (CompoundTag) stack.getOrCreateTag().get("Fluid");
			if (fluidTag == null) {
				return 0;
			}
			return MathHelper.floor((fluidTag.getInt("value") * 1.3F) / fluidTag.getInt("max"));
		}
	};
	public static final Item TINY_BUCKET = new Item(new Item.Settings().group(ItemGroup.MISC).maxCount(1)) {
		@Override
		public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
			if (!stack.getOrCreateTag().contains("Fluid")) {
				stack.putSubTag("Fluid", new Ctx(0, 1620 / 9).toTag());
			}
		}
	};

	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier("fabric-events-interaction-v0-testmod", "big_bucket"), BIG_BUCKET);
		Registry.register(Registry.ITEM, new Identifier("fabric-events-interaction-v0-testmod", "tiny_bucket"), TINY_BUCKET);
		InventoryClickEvents.CLICKED.register((itemStack, cursorStack, slot, screenHandler, clickType, player, playerInventory) -> {
			if (clickType == ClickType.RIGHT) {
				System.out.println("Right Clicked!");
			} else if (clickType == ClickType.LEFT) {
				System.out.println("Left Clicked!");
			}

			if (this.isInvalid(cursorStack)) {
				return ActionResult.PASS;
			}

			// Insert fluid into itemStack
			if (clickType == ClickType.RIGHT) {
				if (itemStack.isOf(BIG_BUCKET) || itemStack.isOf(TINY_BUCKET)) {
					Ctx ctx = Ctx.fromStack(itemStack);

					if (cursorStack.isOf(Items.POTION) || cursorStack.isOf(Items.WATER_BUCKET)) {
						int am = this.getFill(cursorStack);

						if (ctx.canInsert(am)) {
							cursorStack.decrement(1);
							ItemStack newStack = new ItemStack(cursorStack.isOf(Items.POTION) ? Items.GLASS_BOTTLE : Items.BUCKET);

							if (!playerInventory.insertStack(newStack)) {
								if (!player.world.isClient()) {
									ItemScatterer.spawn(player.world, player.getX(), player.getY(), player.getZ(), newStack);
								}
							}

							itemStack.putSubTag("Fluid", ctx.insert(am).toTag());
						} else {
							return ActionResult.FAIL;
						}
					} else if (cursorStack.isOf(TINY_BUCKET) || cursorStack.isOf(BIG_BUCKET)) {
						Ctx otherCtx = Ctx.fromStack(cursorStack);

						if(ctx.canInsert(otherCtx.getValue())) {
							cursorStack.putSubTag("Fluid", otherCtx.drainAll().toTag());
							itemStack.putSubTag("Fluid", ctx.insert(otherCtx.getValue()).toTag());
						}
					}
				} else if (itemStack.isOf(Items.GLASS_BOTTLE) || itemStack.isOf(Items.BUCKET)) {
					if (itemStack.isOf(BIG_BUCKET)) {
						int fill = cursorStack.isOf(Items.GLASS_BOTTLE) ? 540 : 1620;
						Ctx ctx = Ctx.fromStack(cursorStack);

						if (ctx.canDrain(fill)) {
							itemStack.decrement(1);
							ItemStack newStack = new ItemStack(cursorStack.isOf(Items.GLASS_BOTTLE) ? Items.POTION : Items.WATER_BUCKET);

							if (newStack.isOf(Items.POTION)) {
								PotionUtil.setPotion(newStack, Potions.WATER);
							}

							if (!playerInventory.insertStack(newStack)) {
								if (!player.world.isClient()) {
									ItemScatterer.spawn(player.world, player.getX(), player.getY(), player.getZ(), newStack);
								}
							}

							cursorStack.putSubTag("Fluid", ctx.drain(fill).toTag());
						} else {
							return ActionResult.FAIL;
						}
					}
				}
			}

			return ActionResult.PASS;
		});
	}

	private boolean isInvalid(ItemStack stack) {
		boolean potion = stack.getItem() == Items.POTION && PotionUtil.getPotion(stack) == Potions.WATER;
		return !stack.isIn(CLICKABLES) || !potion;
	}

	private int getFill(ItemStack stack) {
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
			if (this.isInvalid(stack)) {
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
		} else if (f != 0.0F) {
			return Formatting.AQUA;
		}

		return Formatting.WHITE;
	}

	private static final class Ctx {
		public static final Codec<Ctx> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.INT.fieldOf("value").forGetter(Ctx::getValue),
				Codec.INT.fieldOf("max").forGetter(Ctx::getMax)
		).apply(instance, Ctx::new));
		private final int value;
		private final int max;

		private Ctx(int value, int max) {
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

		public Ctx drainAll() {
			return new Ctx(this.max, 0);
		}

		public Ctx drain(int amount) {
			return new Ctx(this.max, this.value - amount);
		}

		public Ctx insert(int amount) {
			return new Ctx(this.max, this.value + amount);
		}

		public static Ctx fromTag(CompoundTag tag) {
			return NbtOps.INSTANCE.withParser(CODEC).apply(tag).getOrThrow(false, System.err::println);
		}

		public static Ctx fromStack(ItemStack stack) {
			return fromTag(Optional.ofNullable(stack.getSubTag("Fluid")).orElse(new Ctx(0, 1620).toTag()));
		}
	}
}
