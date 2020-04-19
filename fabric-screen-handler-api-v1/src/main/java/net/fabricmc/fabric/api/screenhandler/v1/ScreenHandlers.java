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

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType;

/**
 * An utility for creating screen handler types.
 */
public final class ScreenHandlers {
	private ScreenHandlers() {
	}

	/**
	 * Creates a new {@code ScreenHandlerType} that creates client-sided screen handlers using the factory.
	 *
	 * @param factory the client-sided screen handler factory
	 * @param <T>     the screen handler type
	 * @return the created type object
	 */
	public static <T extends ScreenHandler> ScreenHandlerType<T> simple(SimpleFactory<T> factory) {
		// Wrap our factory in vanilla's factory; it will not be public for users.
		return new ScreenHandlerType<>(factory::create);
	}

	/**
	 * Creates a new {@code ScreenHandlerType} that creates client-sided screen handlers with additional
	 * networked opening data.
	 *
	 * <p>These screen handlers must be opened with a {@link ExtendedScreenHandlerFactory}.
	 *
	 * @param factory the client-sided screen handler factory
	 * @param <T>     the screen handler type
	 * @return the created type object
	 */
	public static <T extends ScreenHandler> ScreenHandlerType<T> extended(ExtendedFactory<T> factory) {
		return new ExtendedScreenHandlerType<>(factory);
	}

	/**
	 * A factory for client-sided screen handler instances.
	 *
	 * @param <T> the screen handler type
	 */
	public interface SimpleFactory<T extends ScreenHandler> {
		/**
		 * Creates a new client-sided screen handler.
		 *
		 * @param syncId    the synchronization ID
		 * @param inventory the player inventory
		 * @return the created screen handler
		 */
		@Environment(EnvType.CLIENT)
		T create(int syncId, PlayerInventory inventory);
	}

	/**
	 * A factory for client-sided screen handler instances
	 * with additional synced opening data.
	 *
	 * @param <T> the screen handler type
	 * @see ExtendedScreenHandlerFactory
	 */
	public interface ExtendedFactory<T extends ScreenHandler> {
		/**
		 * Creates a new client-sided screen handler with additional opening data.
		 *
		 * @param syncId    the synchronization ID
		 * @param inventory the player inventory
		 * @param buf       the packet buffer
		 * @return the created screen handler
		 */
		@Environment(EnvType.CLIENT)
		T create(int syncId, PlayerInventory inventory, PacketByteBuf buf);
	}
}
