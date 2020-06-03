package net.fabricmc.fabric.api.fluids.v1;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
import net.fabricmc.fabric.api.fluids.v1.world.SidedFluidContainer;

/**
 * An util class for accessing fluid containers.
 *
 * @see Drops
 */
public class FluidView {
	private FluidView() {
	}

	/**
	 * Get the fluid container for the given stack.
	 *
	 * @param sink a place for the item to put it's byproducts into, and take items when being filled
	 * @return the fluid container for the stack
	 */
	public static FluidContainer getFluidContainer(ItemStack stack, ItemSink sink) {
		Item item = stack.getItem();

		if (item instanceof ItemFluidContainer) {
			return ((ItemFluidContainer) item).getContainer(sink, stack);
		}

		return ImmutableFluidVolume.EMPTY;
	}

	/**
	 * Merge all the entity and block containers in the given location for the given face into one fluid container.
	 *
	 * @param face the face to access
	 * @return a fluid container for the face
	 */
	public static FluidContainer getFluidContainer(World world, BlockPos pos, Direction face) {
		FluidContainer container = getBlockFluidContainer(world, pos, face);
		Iterable<FluidContainer> entities = getEntityFluidContainersIterable(world, pos, face);

		if (Iterables.isEmpty(entities)) {
			return container;
		}

		if ((container.isImmutable() && container.isEmpty())) {
			return new SimpleFluidContainer(entities);
		} else {
			return new SimpleFluidContainer(Iterables.concat(() -> Iterators.singletonIterator(container), getEntityFluidContainersIterable(world, pos, face)));
		}
	}

	/**
	 * Get the fluid container for the block at the given location at the given side.
	 *
	 * @param face the face to access
	 * @return the fluid container
	 */
	public static FluidContainer getBlockFluidContainer(World world, BlockPos pos, Direction face) {
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		if (block instanceof SidedFluidContainer) {
			return ((SidedFluidContainer) block).getContainer(world, pos, face);
		}

		return ImmutableFluidVolume.EMPTY;
	}

	private static Iterable<FluidContainer> getEntityFluidContainersIterable(World world, BlockPos pos, Direction face) {
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

	public static FluidContainer getEntityFluidContainer(World world, BlockPos pos, Direction face) {
		return new SimpleFluidContainer(getEntityFluidContainersIterable(world, pos, face));
	}

	public static boolean mixable(Fluid a, Fluid b) {
		return Fluids.EMPTY.equals(a) || Fluids.EMPTY.equals(b) || Objects.equals(a, b);
	}

	public static Fluid tryFindNonEmpty(Fluid a, Fluid b) {
		return Fluids.EMPTY.equals(a) ? b : a;
	}
}
