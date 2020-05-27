package net.fabricmc.fabric.test.dimension;

import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.dimension.v1.ChunkGeneratorFactory;
import net.fabricmc.fabric.api.dimension.v1.EntityPlacer;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensionType;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;

public class FabricDimensionTest implements ModInitializer {

	private static RegistryKey<DimensionType> dimensionRegistryKey;

	@Override
	public void onInitialize() {
		dimensionRegistryKey = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, new Identifier("fabric_dimension", "test"));

		FabricDimensionType.builder()
				.chunkGenerator(seed -> new SurfaceChunkGenerator(new VanillaLayeredBiomeSource(seed, false, false), seed, ChunkGeneratorType.Preset.OVERWORLD.getChunkGeneratorType()))
				.defaultPlacer(FabricDimensionTest::placeEntity)
				.buildAndRegister(dimensionRegistryKey);

		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
				dispatcher.register(literal("fabric_dimension_test").executes(FabricDimensionTest.this::executeTestCommand))
		);
	}

	private int executeTestCommand(CommandContext<ServerCommandSource> context) {
		try {
			ServerPlayerEntity serverPlayerEntity = context.getSource().getPlayer();
			ServerWorld serverWorld = serverPlayerEntity.getServerWorld();

			if (!serverWorld.method_27983().equals(dimensionRegistryKey)) {
				FabricDimensions.teleport(serverPlayerEntity, dimensionRegistryKey, FabricDimensionTest::placeEntity);
			} else {
				FabricDimensions.teleport(serverPlayerEntity, DimensionType.OVERWORLD_REGISTRY_KEY, FabricDimensionTest::placeEntity);
			}

		} catch (CommandSyntaxException e) {
			return 0;
		}
		return 1;
	}

	private static BlockPattern.TeleportTarget placeEntity(Entity teleported, ServerWorld destination, Direction portalDir, double horizontalOffset, double verticalOffset) {
		return new BlockPattern.TeleportTarget(new Vec3d(0, 100, 0), Vec3d.ZERO, 0);
	}
}
