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
		CompoundTag fluidTag = (CompoundTag) stack.getOrCreateTag().get("Fluid");
		if (fluidTag == null) {
			return 0;
		}
		return MathHelper.floor((fluidTag.getInt("value") * 1.3F) / fluidTag.getInt("max"));
	}

	public int getMax() {
		return this.max;
	}
}
