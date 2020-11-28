package net.fabricmc.fabric.test.event.interaction;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class FluidContainerItem extends Item {
	private final int max;

	public FluidContainerItem(Settings settings, int max) {
		super(settings);
		this.max = max;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		if (!stack.getOrCreateTag().contains("Fluid")) {
			stack.putSubTag("Fluid", new InventoryClickTests.Ctx(0, this.max).toTag());
		}
	}

	@Override
	public boolean hasGlint(ItemStack stack) {
		return true;
	}

	public int getMax() {
		return this.max;
	}
}
