/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.test.dimension;

import static net.minecraft.entity.EntityType.COW;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.block.Blocks;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class FabricDimensionTest implements ModInitializer {
	// The dimension options refer to the JSON-file in the dimension subfolder of the data pack,
	// which will always share its ID with the world that is created from it
	private static final RegistryKey<DimensionOptions> DIMENSION_KEY = RegistryKey.of(RegistryKeys.DIMENSION, new Identifier("fabric_dimension", "void"));
	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.literal("Teleportation failed!"));

	private static RegistryKey<World> WORLD_KEY = RegistryKey.of(RegistryKeys.WORLD, DIMENSION_KEY.getValue());

	@Override
	public void onInitialize() {
		Registry.register(Registries.CHUNK_GENERATOR, new Identifier("fabric_dimension", "void"), VoidChunkGenerator.CODEC);

		WORLD_KEY = RegistryKey.of(RegistryKeys.WORLD, new Identifier("fabric_dimension", "void"));

		if (System.getProperty("fabric-api.gametest") != null) {
			// The gametest server does not support custom worlds
			return;
		}

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			ServerWorld overworld = server.getWorld(World.OVERWORLD);
			ServerWorld world = server.getWorld(WORLD_KEY);

			if (world == null) throw new AssertionError("Test world doesn't exist.");

			Entity entity = COW.create(overworld);

			if (entity == null) throw new AssertionError("Could not create entity!");
			if (!entity.getWorld().getRegistryKey().equals(World.OVERWORLD)) throw new AssertionError("Entity starting world isn't the overworld");

			TeleportTarget target = new TeleportTarget(world, Vec3d.ZERO, new Vec3d(1, 1, 1), 45f, 60f);

			Entity teleported = FabricDimensions.teleport(entity, target);

			if (teleported == null) throw new AssertionError("Entity didn't teleport");

			if (!teleported.getWorld().getRegistryKey().equals(WORLD_KEY)) throw new AssertionError("Target world not reached.");

			if (!teleported.getPos().equals(target.position())) throw new AssertionError("Target Position not reached.");
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(literal("fabric_dimension_test")
					.executes(FabricDimensionTest.this::swapTargeted));

			// Used to test https://github.com/FabricMC/fabric/issues/2239
			// Dedicated-only
			if (environment != CommandManager.RegistrationEnvironment.INTEGRATED) {
				dispatcher.register(literal("fabric_dimension_test_desync")
						.executes(FabricDimensionTest.this::testDesync));
			}

			// Used to test https://github.com/FabricMC/fabric/issues/2238
			dispatcher.register(literal("fabric_dimension_test_entity")
					.executes(FabricDimensionTest.this::testEntityTeleport));

			// Used to test teleport to vanilla dimension
			dispatcher.register(literal("fabric_dimension_test_tp")
					.then(argument("target", DimensionArgumentType.dimension())
					.executes((context) ->
							testVanillaTeleport(context, DimensionArgumentType.getDimensionArgument(context, "target")))));
		});
	}

	private int swapTargeted(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();

		if (player == null) {
			context.getSource().sendFeedback(() -> Text.literal("You must be a player to execute this command."), false);
			return 1;
		}

		ServerWorld serverWorld = player.getServerWorld();
		ServerWorld modWorld = getModWorld(context);

		if (serverWorld != modWorld) {
			TeleportTarget target = new TeleportTarget(modWorld, new Vec3d(0.5, 101, 0.5), Vec3d.ZERO, 0, 0);
			FabricDimensions.teleport(player, target);

			if (player.getWorld() != modWorld) {
				throw FAILED_EXCEPTION.create();
			}

			modWorld.setBlockState(new BlockPos(0, 100, 0), Blocks.DIAMOND_BLOCK.getDefaultState());
			modWorld.setBlockState(new BlockPos(0, 101, 0), Blocks.TORCH.getDefaultState());
		} else {
			TeleportTarget target = new TeleportTarget(getWorld(context, World.OVERWORLD), new Vec3d(0, 100, 0), Vec3d.ZERO,
					(float) Math.random() * 360 - 180, (float) Math.random() * 360 - 180);
			FabricDimensions.teleport(player, target);
		}

		return 1;
	}

	private int testDesync(CommandContext<ServerCommandSource> context) {
		ServerPlayerEntity player = context.getSource().getPlayer();

		if (player == null) {
			context.getSource().sendFeedback(() -> Text.literal("You must be a player to execute this command."), false);
			return 1;
		}

		TeleportTarget target = new TeleportTarget((ServerWorld) player.getWorld(), player.getPos().add(5, 0, 0), player.getVelocity(), player.getYaw(), player.getPitch());
		FabricDimensions.teleport(player, target);

		return 1;
	}

	private int testEntityTeleport(CommandContext<ServerCommandSource> context) {
		ServerPlayerEntity player = context.getSource().getPlayer();

		if (player == null) {
			context.getSource().sendFeedback(() -> Text.literal("You must be a player to execute this command."), false);
			return 1;
		}

		Entity entity = player.getWorld()
				.getOtherEntities(player, player.getBoundingBox().expand(100, 100, 100))
				.stream()
				.findFirst()
				.orElse(null);

		if (entity == null) {
			context.getSource().sendFeedback(() -> Text.literal("No entities found."), false);
			return 1;
		}

		TeleportTarget target = new TeleportTarget((ServerWorld) entity.getWorld(), player.getPos(), player.getVelocity(), player.getYaw(), player.getPitch());
		FabricDimensions.teleport(entity, target);

		return 1;
	}

	private int testVanillaTeleport(CommandContext<ServerCommandSource> context, ServerWorld targetWorld) throws CommandSyntaxException {
		Entity entity = context.getSource().getEntityOrThrow();
		TeleportTarget target = new TeleportTarget(targetWorld, entity.getPos(), entity.getVelocity(), entity.getYaw(), entity.getPitch());
		FabricDimensions.teleport(entity, target);

		return 1;
	}

	private ServerWorld getModWorld(CommandContext<ServerCommandSource> context) {
		return getWorld(context, WORLD_KEY);
	}

	private ServerWorld getWorld(CommandContext<ServerCommandSource> context, RegistryKey<World> dimensionRegistryKey) {
		return context.getSource().getServer().getWorld(dimensionRegistryKey);
	}
}
