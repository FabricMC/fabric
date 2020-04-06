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

package net.fabricmc.fabric.api.screenhandler.v1;

import java.util.function.Consumer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

/**
 * An extension of {@code NamedScreenHandlerFactory} that can write additional
 * networking data to a screen opening packet.
 *
 * @see ScreenHandlers#extended(ScreenHandlers.ExtendedFactory)
 */
public interface ExtendedScreenHandlerFactory extends NamedScreenHandlerFactory {
	/**
	 * Writes additional server -> client screen opening data to the buffer.
	 */
	void writeScreenData(PacketByteBuf buf);

	/**
	 * Creates a networked screen handler factory from a base factory and a packet writer.
	 *
	 * <p>All {@link NamedScreenHandlerFactory} operations are delegated to the base factory,
	 * and {@link #writeScreenData(PacketByteBuf)} is delegated to the writer.
	 *
	 * @param factory      the base factory
	 * @param packetWriter the packet writer
	 * @return the created factory
	 */
	static ExtendedScreenHandlerFactory of(NamedScreenHandlerFactory factory, Consumer<PacketByteBuf> packetWriter) {
		return new ExtendedScreenHandlerFactory() {
			@Override
			public ScreenHandler createMenu(int syncId, PlayerInventory inventory, PlayerEntity player) {
				return factory.createMenu(syncId, inventory, player);
			}

			@Override
			public Text getDisplayName() {
				return factory.getDisplayName();
			}

			@Override
			public void writeScreenData(PacketByteBuf buf) {
				packetWriter.accept(buf);
			}
		};
	}
}
