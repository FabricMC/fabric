package net.fabricmc.fabric.api.fluids.v1.minecraft.items;

import static net.fabricmc.fabric.api.fluids.v1.minecraft.FluidIds.DRAGONS_BREATH;
import static net.fabricmc.fabric.api.fluids.v1.minecraft.FluidIds.EMPTY;
import static net.fabricmc.fabric.api.fluids.v1.minecraft.FluidIds.EXPERIENCE;
import static net.fabricmc.fabric.api.fluids.v1.minecraft.FluidIds.HONEY;
import static net.minecraft.item.Items.DRAGON_BREATH;
import static net.minecraft.item.Items.EXPERIENCE_BOTTLE;
import static net.minecraft.item.Items.GLASS_BOTTLE;
import static net.minecraft.item.Items.HONEY_BOTTLE;
import static net.minecraft.item.Items.POTION;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.fluids.v1.item.ItemSink;
import net.fabricmc.fabric.api.fluids.v1.math.Drops;
import net.fabricmc.fabric.api.fluids.v1.minecraft.FluidIds;

public class BottleItemFluidContainer extends UnitItemFluidContainer {
	private static final BiMap<Item, Identifier> BOTTLES = HashBiMap.create();
	private static final long ONE_THIRD = Drops.fraction(1, 3);

	static {
		BOTTLES.put(GLASS_BOTTLE, EMPTY);
		BOTTLES.put(HONEY_BOTTLE, HONEY);
		BOTTLES.put(EXPERIENCE_BOTTLE, EXPERIENCE);
		BOTTLES.put(POTION, FluidIds.POTION);
		BOTTLES.put(DRAGON_BREATH, DRAGONS_BREATH);
	}

	public BottleItemFluidContainer(ItemStack stack, ItemSink output) {
		super(stack, output);
	}

	@Override
	protected long unit() {
		return ONE_THIRD;
	}

	@Override
	protected Identifier getFluid() {
		return BOTTLES.get(this.stack.getItem());
	}

	@Override
	protected boolean empty() {
		return this.stack.getItem() == GLASS_BOTTLE;
	}

	@Override
	protected Item consumeOnAdd() {
		return GLASS_BOTTLE;
	}

	@Override
	protected void addFilled(ItemSink sink, Identifier fluid, int items, boolean simulate) {
		sink.push(new ItemStack(BOTTLES.inverse().get(fluid), items), simulate);
	}
}
