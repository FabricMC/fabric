package net.fabricmc.fabric.mixin.fluids;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.fluids.items.ItemSink;

@Mixin (PlayerInventory.class)
public abstract class PlayerInventoryMixin implements ItemSink {

	@Shadow @Final public DefaultedList<ItemStack> main;
	@Shadow @Final public PlayerEntity player;

	@Override
	public ItemStack take(ItemStack stack, boolean simulate) {
		if (stack.isEmpty()) {
			return stack;
		}

		int i;
		int count = stack.getCount();
		while ((i = this.getSlot(stack)) != -1) {
			count -= this.removeStack(i, count).getCount();
			if (count <= 0) {
				return stack.copy();
			}
		}
		ItemStack copy = stack.copy();
		copy.setCount(stack.getCount() - count);
		return copy;
	}

	@Override
	public void push(ItemStack stack, boolean simulate) {
		if (!simulate) {
			this.offerOrDrop(this.player.world, stack);
		}
	}

	@Shadow
	public abstract void offerOrDrop(World world, ItemStack stack);

	@Unique
	public int getSlot(ItemStack stack) {
		for (int i = 0; i < this.main.size(); ++i) {
			if (!this.main.get(i).isEmpty() && this.areItemsEqual(stack, this.main.get(i))) {
				return i;
			}
		}

		return -1;
	}

	@Shadow
	public abstract ItemStack removeStack(int slot, int amount);

	@Shadow
	protected abstract boolean areItemsEqual(ItemStack stack1, ItemStack stack2);

}
