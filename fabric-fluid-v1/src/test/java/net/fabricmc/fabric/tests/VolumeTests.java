package net.fabricmc.fabric.tests;

import static net.fabricmc.fabric.api.fluid.v1.math.Drops.fraction;

import java.util.Objects;

import net.minecraft.Bootstrap;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundTag;

import net.fabricmc.fabric.api.fluid.v1.Action;
import net.fabricmc.fabric.api.fluid.v1.container.FluidContainer;
import net.fabricmc.fabric.api.fluid.v1.container.SimpleFluidContainer;
import net.fabricmc.fabric.api.fluid.v1.container.volume.FixedSizeFluidVolume;
import net.fabricmc.fabric.api.fluid.v1.container.volume.FluidVolume;
import net.fabricmc.fabric.api.fluid.v1.container.volume.FluidVolumeDelegate;
import net.fabricmc.fabric.api.fluid.v1.container.volume.ImmutableFluidVolume;
import net.fabricmc.fabric.api.fluid.v1.container.volume.SimpleFluidVolume;
import net.fabricmc.fabric.api.fluid.v1.math.Drops;

/**
 * can I please just use JUnit or something.
 */
public class VolumeTests {
	public static void main(String[] args) {
		Bootstrap.initialize();
		testFluidContainer(new SimpleFluidVolume());
		testFluidContainer(new FluidVolumeDelegate(new SimpleFluidVolume()));
		testFixedSize(new FixedSizeFluidVolume(fraction(1, 2)));
		testFixedSize(new SimpleFluidContainer(new FixedSizeFluidVolume(fraction(1, 4)), new FixedSizeFluidVolume(fraction(1, 4))));
		serializeTest(new SimpleFluidVolume(Fluids.WATER, 10));
		serializeTest(new SimpleFluidVolume(Fluids.WATER, 0));
		CompoundTag test = new CompoundTag();
		test.putInt("e", 1);
		serializeTest(new SimpleFluidVolume(Fluids.WATER, 10, test));
		serializeTest(new FixedSizeFluidVolume(Fluids.WATER, 10, 10));

		testBasics(new SimpleFluidContainer(of(Fluids.WATER, 1)));
		testBasics(new FixedSizeFluidVolume(Fluids.WATER, 1, 1));
		testBasics(of(Fluids.WATER, 1));
		testBasics(new FluidVolumeDelegate(of(Fluids.WATER, 1)));
		testBasics(new ImmutableFluidVolume(Fluids.WATER, 1));
		testBasics(new FixedSizeFluidVolume(Fluids.WATER, 1, new CompoundTag(), 1));
	}

	private static void testBasics(FluidContainer container) {
		test("empty", !container.isEmpty());
		for (FluidVolume volume : container) ;
		equals("total", container.getTotalVolume(), 1L);
		test("mutable", container instanceof ImmutableFluidVolume || !container.isImmutable());
		equals("copy", container, container.simpleCopy());
		if (container instanceof FluidVolume) {
			equals("fluid", ((FluidVolume) container).getFluid(), Fluids.WATER);
			equals("amount", container.getTotalVolume(), ((FluidVolume) container).getAmount());
			equals("compound", ((FluidVolume) container).getData(), new CompoundTag());
		}
	}

	private static void serializeTest(SimpleFluidVolume volumes) {
		test("round_trip", new FixedSizeFluidVolume(volumes.toTag(new CompoundTag()), 1).equals(volumes));
	}

	// tests a half bucket sized fluid volume.
	private static void testFixedSize(FluidContainer volume) {
		test("start_empty", volume.isEmpty());
		// nothing should be in it
		equals("drain_empty", volume.drain(Drops.getBucket(), Action.PERFORM), ImmutableFluidVolume.EMPTY);
		// 1/2 should be leftover after this
		equals("fill_overflow", volume.add(Fluids.WATER, Drops.getBucket(), Action.PERFORM), of(Fluids.WATER, fraction(1, 2)));
		// no lava should be in it
		equals("drain_lava", volume.drain(Fluids.LAVA, Drops.getBucket(), Action.PERFORM), ImmutableFluidVolume.EMPTY);
		// 1/2 bucket of water should still be in it, and after it's done, 1/6 of a bucket
		equals("drain_water", volume.drain(Fluids.WATER, fraction(1, 3), Action.PERFORM), of(Fluids.WATER, fraction(1, 3)));
		// attempt to insert lava, should return everything, unless it's a simple fluid container
		if (volume instanceof SimpleFluidContainer) {
			equals("add_lava", volume.add(Fluids.LAVA, fraction(1, 3), Action.PERFORM), of(Fluids.LAVA, fraction(1, 12)));
			equals("drain_lava2", volume.drain(Fluids.LAVA, Drops.getBucket(), Action.PERFORM), of(Fluids.LAVA, fraction(1, 4)));
		} else {
			equals("add_lava", volume.add(Fluids.LAVA, fraction(1, 3), Action.PERFORM), of(Fluids.LAVA, fraction(1, 3)));
		}
		// container should have 1/6 of a bucket, try to drain 1 entire bucket
		test("partial_fill", !volume.isEmpty());
		equals("drain_water", volume.drain(Fluids.WATER, Drops.getBucket(), Action.PERFORM), of(Fluids.WATER, fraction(1, 6)));
		// container should be empty
		test("end_empty", volume.isEmpty());
	}


	private static void testFluidContainer(FluidContainer container) {
		test("start_empty", container.isEmpty());
		// nothing should be in it
		equals("drain_empty", container.drain(Drops.getBucket(), Action.PERFORM), ImmutableFluidVolume.EMPTY);
		// none should be leftover
		equals("fill_empty", container.add(Fluids.WATER, Drops.getBucket(), Action.PERFORM), ImmutableFluidVolume.EMPTY);
		// no lava should be in it
		equals("drain_lava", container.drain(Fluids.LAVA, Drops.getBucket(), Action.PERFORM), ImmutableFluidVolume.EMPTY);
		// 1 bucket of water should still be in it
		equals("drain_water", container.drain(Fluids.WATER, fraction(1, 3), Action.PERFORM), of(Fluids.WATER, fraction(1, 3)));
		// attempt to insert lava, should return everything
		equals("add_lava", container.add(Fluids.LAVA, fraction(1, 3), Action.PERFORM), of(Fluids.LAVA, fraction(1, 3)));
		// container should have 2/3 of a bucket, try to drain 1 entire bucket
		test("partial_fill", !container.isEmpty());
		equals("drain_water", container.drain(Fluids.WATER, Drops.getBucket(), Action.PERFORM), of(Fluids.WATER, fraction(2, 3)));
		// container should be empty
		test("end_empty", container.isEmpty());
	}

	private static void test(String test, boolean value) {
		if (value) {
			System.out.println("Test passed: " + test);
		} else {
			System.err.println("Test failed: " + test);
			new Throwable().printStackTrace();
		}
	}

	private static void equals(String test, Object a, Object b) {
		boolean val = Objects.equals(a, b);
		test(test, val);
		if (!val) {
			System.err.println(a + " vs " + b);
		}
	}

	private static FluidVolume of(Fluid fluid, long amount) {
		return new SimpleFluidVolume(fluid, amount);
	}
}
