package net.fabricmc.fabric.api.fluids.v1.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.Action;

public class ItemSinks {
	public static ItemSink playerItemSink(PlayerEntity entity) {
		return new ItemSink() {
			private final PlayerInventory inventory = entity.inventory;

			@Override
			public ItemStack take(ItemStack stack, Action action) {
				if (stack.isEmpty()) {
					return stack;
				}

				int i;
				int count = stack.getCount();

				while ((i = this.getSlotWithStack(stack)) != -1) {
					count -= this.inventory.removeStack(i, count).getCount();

					if (count <= 0) {
						return stack.copy();
					}
				}

				ItemStack copy = stack.copy();
				copy.setCount(stack.getCount() - count);
				return copy;
			}

			/**
			 * @see PlayerInventory#getSlotWithStack(ItemStack)
			 */
			public int getSlotWithStack(ItemStack stack) {
				for(int i = 0; i < this.inventory.main.size(); ++i) {
					if (!this.inventory.main.get(i).isEmpty() && this.areItemsEqual(stack, this.inventory.main.get(i))) {
						return i;
					}
				}

				return -1;
			}

			/**
			 * @see PlayerInventory#areItemsEqual(ItemStack, ItemStack)
			 */
			private boolean areItemsEqual(ItemStack stack1, ItemStack stack2) {
				return stack1.getItem() == stack2.getItem() && ItemStack.areTagsEqual(stack1, stack2);
			}

			@Override
			public void push(ItemStack stack, Action action) {
				if (action.perform()) {
					this.inventory.offerOrDrop(entity.world, stack);
				}
			}
		};
	}
}
