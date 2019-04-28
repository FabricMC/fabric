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
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ContainerMod implements ModInitializer {

	public static final Identifier EXAMPLE_CONTAINER = new Identifier("fabric_container", "example_container");
	public static final Identifier EXAMPLE_CONTAINER_2 = new Identifier("fabric_container", "example_container_2");
	public static final Identifier EXAMPLE_INVENTORY_CONTAINER = new Identifier("fabric_container", "example_inventory_container");

	@Override
	public void onInitialize() {
		//Registers a basic server side command that shows that the openContainer works from the server side.
		CommandRegistry.INSTANCE.register(false, serverCommandSourceCommandDispatcher ->
			serverCommandSourceCommandDispatcher.register(CommandManager
				.literal("container")
				.executes(context -> {
					BlockPos pos = new BlockPos(context.getSource().getEntity());

					//Opens a container, sending the block pos
					ContainerProviderRegistry.INSTANCE.openContainer(EXAMPLE_INVENTORY_CONTAINER, context.getSource().getPlayer(), buf -> buf.writeBlockPos(pos));

					return 1;
				})));

		//Registers a container factory that opens our example Container, this reads the block pos from the buffer
		ContainerProviderRegistry.INSTANCE.registerFactory(EXAMPLE_CONTAINER, (syncId, identifier, player, buf) -> {
			BlockPos pos = buf.readBlockPos();
			return new ExampleContainer(syncId, pos, player);
		});
		ContainerProviderRegistry.INSTANCE.registerFactory(EXAMPLE_CONTAINER_2, (syncId, identifier, player, buf) -> {
			BlockPos pos = buf.readBlockPos();
			return new ExampleContainer(syncId, pos, player);
		});
		ContainerProviderRegistry.INSTANCE.registerFactory(EXAMPLE_INVENTORY_CONTAINER, (syncId, identifier, player, buf) -> {
			return new ExampleInventoryContainer(syncId, player);
		});
	}

	//A basic container that prints to console when opened, this should print on the client + server
	public static class ExampleContainer extends Container {
		public final PlayerInventory playerInventory;
		BlockPos pos;

		public ExampleContainer(int syncId, BlockPos pos, PlayerEntity playerEntity) {
			super(null, syncId);
			this.pos = pos;
			this.playerInventory = playerEntity.inventory;
			System.out.println("Opened container, " + pos);
		}

		@Override
		public boolean canUse(PlayerEntity playerEntity) {
			return true;
		}
	}

	public static class ExampleInventoryContainer extends Container {
		public final PlayerInventory playerInventory;
		BlockPos pos;

		public ExampleInventoryContainer(int syncId, PlayerEntity playerEntity) {
			super(null, syncId);
			this.playerInventory = playerEntity.inventory;
			for (int i = 0; i < 3; ++i) {
				for (int j = 0; j < 9; ++j) {
					this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
				}
			}

			for (int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 142));
			}
		}

		@Override
		public boolean canUse(PlayerEntity playerEntity) {
			return true;
		}
	}
}
