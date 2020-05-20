package net.fabricmc.fabric.test.dimension;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensionType;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.class_5284;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;

public class FabricDimensionTest implements ModInitializer {
	@Override
	public void onInitialize() {

		FabricDimensionType.builder()
				.chunkGenerator(new SurfaceChunkGenerator(new VanillaLayeredBiomeSource(0, false, false), 0, class_5284.class_5307.field_24789.method_28568()))
				.defaultPlacer((teleported, destination, portalDir, horizontalOffset, verticalOffset) -> new BlockPattern.TeleportTarget(new Vec3d(0, 100, 0), Vec3d.ZERO, 0))
				.buildAndRegister(new Identifier("fabric_dimension", "test"));
	}
}
