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

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.gui.GuiProviderRegistry;
import net.minecraft.client.gui.ContainerGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public class ContainerModClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		//Registers a gui factory that opens our example gui, this reads the block pos from the buffer
		GuiProviderRegistry.INSTANCE.registerFactory(ContainerMod.EXAMPLE_CONTAINER, (player, buf) -> {
			BlockPos pos = buf.readBlockPos();
			return new ExampleContainerGui(pos, player);
		});
	}

	//A container gui that shows the block pos that was sent
	public static class ExampleContainerGui extends ContainerGui {

		BlockPos pos;

		public ExampleContainerGui(BlockPos pos, PlayerEntity playerEntity) {
			super(new ContainerMod.ExampleContainer(pos, playerEntity));
			this.pos = pos;
		}

		@Override
		protected void drawBackground(float v, int i, int i1) {
			fontRenderer.draw(pos.toString(), width / 2, height / 2, 0);
		}
	}

}
