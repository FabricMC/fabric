/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.containers;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.commands.CommandRegistry;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ContainerMod implements ModInitializer {

	public static final Identifier EXAMPLE_CONTAINER = new Identifier("fabric_container", "example_container");

	@Override
	public void onInitialize() {
		//Registers a basic server side command that shows that the openContainer works from the server side.
		CommandRegistry.INSTANCE.register(false, serverCommandSourceCommandDispatcher ->
			serverCommandSourceCommandDispatcher.register(ServerCommandManager
				.literal("container")
				.executes(context -> {
					BlockPos pos = context.getSource().getEntity().getPos();

					//Opens a container, sending the block pos
					ContainerProviderRegistry.INSTANCE.openContainer(EXAMPLE_CONTAINER, context.getSource().getPlayer(), buf -> buf.writeBlockPos(pos));

					return 1;
				})));

		//Registers a container factory that opens our example Container, this reads the block pos from the buffer
		ContainerProviderRegistry.INSTANCE.registerFactory(EXAMPLE_CONTAINER, (player, buf) -> {
			BlockPos pos = buf.readBlockPos();
			return new ExampleContainer(pos, player);
		});

	}

	//A basic container that prints to console when opened, this should print on the client + server
	public static class ExampleContainer extends Container {

		BlockPos pos;

		public ExampleContainer(BlockPos pos, PlayerEntity playerEntity) {
			this.pos = pos;
			System.out.println("Opened container, " + pos);
		}

		@Override
		public boolean canUse(PlayerEntity playerEntity) {
			return true;
		}
	}

}
