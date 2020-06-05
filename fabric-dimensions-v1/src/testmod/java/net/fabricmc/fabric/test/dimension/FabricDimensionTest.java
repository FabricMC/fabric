package net.fabricmc.fabric.test.dimension;

import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.class_5311;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensionType;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;

public class FabricDimensionTest implements ModInitializer {
	private static RegistryKey<World> dimensionRegistryKey;
	private static RegistryKey<World> voidDimensionRegistryKey;

	@Override
	public void onInitialize() {
		dimensionRegistryKey = RegistryKey.of(Registry.DIMENSION, new Identifier("fabric_dimension", "test"));
		voidDimensionRegistryKey = RegistryKey.of(Registry.DIMENSION, new Identifier("fabric_dimension", "void"));

		FabricDimensionType.builder()
				.chunkGenerator(seed -> new SurfaceChunkGenerator(new VanillaLayeredBiomeSource(seed / 2, false, false), seed / 2, ChunkGeneratorType.Preset.OVERWORLD.getChunkGeneratorType()))
				.defaultPlacer(FabricDimensionTest::placeEntity)
				.buildAndRegister(dimensionRegistryKey);

		FabricDimensionType.builder()
				.chunkGenerator(seed -> VoidChunkGenerator.INSTANCE)
				.defaultPlacer(FabricDimensionTest::placeEntityInVoid)
				.buildAndRegister(voidDimensionRegistryKey);

		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
				dispatcher.register(literal("fabric_dimension_test").executes(FabricDimensionTest.this::executeTestCommand))
		);
	}

	private int executeTestCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity serverPlayerEntity = context.getSource().getPlayer();
		ServerWorld serverWorld = serverPlayerEntity.getServerWorld();

		if (!serverWorld.getRegistryKey().equals(dimensionRegistryKey)) {
			serverPlayerEntity.changeDimension(dimensionRegistryKey);
		} else {
			FabricDimensions.teleport(serverPlayerEntity, World.OVERWORLD, FabricDimensionTest::placeEntity);
		}

		return 1;
	}

	private static BlockPattern.TeleportTarget placeEntity(Entity teleported, ServerWorld destination, Direction portalDir, double horizontalOffset, double verticalOffset) {
		return new BlockPattern.TeleportTarget(new Vec3d(0, 100, 0), Vec3d.ZERO, 0);
	}

	private static BlockPattern.TeleportTarget placeEntityInVoid(Entity teleported, ServerWorld destination, Direction portalDir, double horizontalOffset, double verticalOffset) {
		destination.setBlockState(new BlockPos(0, 100, 0), Blocks.DIAMOND_BLOCK.getDefaultState());
		return new BlockPattern.TeleportTarget(new Vec3d(0, 100, 0), Vec3d.ZERO, 0);
	}

	private static class VoidChunkGenerator extends ChunkGenerator {
		public static final VoidChunkGenerator INSTANCE = new VoidChunkGenerator();
		public static final Codec<VoidChunkGenerator> CODEC = Codec.unit(() -> INSTANCE).stable();

		VoidChunkGenerator() {
			super(new FixedBiomeSource(Biomes.PLAINS), new class_5311(false));
		}

		@Override
		protected Codec<? extends ChunkGenerator> method_28506() {
			return CODEC;
		}

		@Override
		public ChunkGenerator withSeed(long seed) {
			return this;
		}

		@Override
		public void buildSurface(ChunkRegion region, Chunk chunk) {
		}

		@Override
		public void populateNoise(WorldAccess world, StructureAccessor accessor, Chunk chunk) {
		}

		@Override
		public int getHeight(int x, int z, Heightmap.Type heightmapType) {
			return 0;
		}

		@Override
		public BlockView getColumnSample(int x, int z) {
			return new VerticalBlockSample(new BlockState[0]);
		}
	}
}
