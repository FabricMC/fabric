package net.fabricmc.fabric.test.fluid;

import net.fabricmc.api.ModInitializer;

public class FluidTest implements ModInitializer {

	@Override
	public void onInitialize() {
		Register.init();
		// testing protocol
		// /give @p fabric-fluid-v1:fluid_shard 48
		// /setblock ~ ~ ~ fabric-fluid-v1-test:block
		// /give @p bucket 16
		// right click the block with the fluid shards, this should insert 16 buckets worth of water into the container
		// then right click the block with the buckets, this should give u 16 buckets worth of water from the container
		// insert the buckets back into the container and repeat the previous step

		// /setblock ~ ~ ~ cauldron
		// /give @p fabric-fluid-v1:fluid_shard 64
		// right click the cauldron, this should consume 3 of the fluid shards

		// /give @p fabric-fluid-v1:fluid_shard 2 {temperature:10d}

	}
}
