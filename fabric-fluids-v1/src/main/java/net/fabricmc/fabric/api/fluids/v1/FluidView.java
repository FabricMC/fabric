package net.fabricmc.fabric.api.fluids.v1;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.fluids.v1.container.FluidContainer;
import net.fabricmc.fabric.api.fluids.v1.container.SimpleFluidContainer;
import net.fabricmc.fabric.api.fluids.v1.container.volume.ImmutableFluidVolume;
import net.fabricmc.fabric.api.fluids.v1.item.ItemFluidContainer;
import net.fabricmc.fabric.api.fluids.v1.item.ItemSink;
import net.fabricmc.fabric.api.fluids.v1.math.Drops;
import net.fabricmc.fabric.api.fluids.v1.minecraft.items.BottleItemFluidContainer;
import net.fabricmc.fabric.api.fluids.v1.minecraft.items.BowlItemFluidContainer;
import net.fabricmc.fabric.api.fluids.v1.world.SidedFluidContainer;

/**
 * A util class for accessing fluid containers.
 *
 * @see Drops
 */
public class FluidView {
	public static final Map<Item, ItemFluidContainer> FLUID_CONTAINERS = new IdentityHashMap<>();

	static {
		FLUID_CONTAINERS.put(Items.BOWL, ((waste, stack) -> new BowlItemFluidContainer(stack, waste)));
		FLUID_CONTAINERS.put(Items.DRAGON_BREATH, ((waste, stack) -> new BottleItemFluidContainer(stack, waste)));
	}

	private FluidView() {
	}

	/**
	 * get the fluid container for the given stack.
	 *
	 * @param stack the itemstack
	 * @param sink a place for the item to put it's byproducts into, and take items when being filled
	 * @return the fluid container for the stack
	 */
	public static FluidContainer getFluidContainer(ItemStack stack, ItemSink sink) {
		Item item = stack.getItem();

		if (item instanceof ItemFluidContainer) {
			return ((ItemFluidContainer) item).getContainer(sink, stack);
		}

		ItemFluidContainer container = FLUID_CONTAINERS.get(item);

		if (container != null) {
			return container.getContainer(sink, stack);
		}

		return ImmutableFluidVolume.EMPTY;
	}

	/**
	 * merge all the entity and block containers in the given location for the given face into one fluid container.
	 *
	 * @param world the world
	 * @param pos the position
	 * @param face the face
	 * @return a fluid container for the face
	 */
	public static FluidContainer getContainer(World world, BlockPos pos, Direction face) {
		FluidContainer container = getBlockContainer(world, pos, face);
		Iterable<FluidContainer> entities = getEntityContainersIterable(world, pos, face);

		if (Iterables.isEmpty(entities)) {
			return container;
		}

		if ((container.isImmutable() && container.isEmpty())) {
			return new SimpleFluidContainer(entities);
		} else {
			return new SimpleFluidContainer(Iterables.concat(() -> Iterators.singletonIterator(container), getEntityContainersIterable(world, pos, face)));
		}
	}

	/**
	 * get the fluid container for the block at the given location at the given side.
	 *
	 * @param world the world
	 * @param pos the position
	 * @param face the face to access
	 * @return the fluid container
	 */
	public static FluidContainer getBlockContainer(World world, BlockPos pos, Direction face) {
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		if (block instanceof SidedFluidContainer) {
			return ((SidedFluidContainer) block).getContainer(world, pos, face);
		}

		return ImmutableFluidVolume.EMPTY;
	}

	public static Iterable<FluidContainer> getEntityContainersIterable(World world, BlockPos pos, Direction face) {
		List<Entity> entities = world.getEntities((Entity) null, new Box(pos), e -> e instanceof SidedFluidContainer);

		if (entities.isEmpty()) {
			return Collections::emptyIterator;
		}

		return () -> new Iterator<FluidContainer>() {
			private Iterator<Entity> iterator = entities.iterator();

			@Override
			public boolean hasNext() {
				return this.iterator.hasNext();
			}

			@Override
			public FluidContainer next() {
				return ((SidedFluidContainer) this.iterator.next()).getContainer(world, pos, face);
			}
		};
	}

	public static FluidContainer getEntityContainer(World world, BlockPos pos, Direction face) {
		return new SimpleFluidContainer(getEntityContainersIterable(world, pos, face));
	}
}
