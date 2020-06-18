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
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.screenhandler.ExtendedScreenHandlerType;

/**
 * An API for creating and registering {@linkplain ScreenHandlerType screen handler types}.
 *
 * <p>This class exposes the private {@link ScreenHandlerType} constructor,
 * as well as adds support for creating types using Fabric's extended screen handler API.
 *
 * <p>Screen handlers types are used to synchronize {@linkplain ScreenHandler screen handlers}
 * between the server and the client. Screen handlers manage the items and integer properties that are
 * needed to show on screens, such as the items in a chest or the progress of a furnace.
 *
 * <h2>Simple and extended screen handlers</h2>
 * Simple screen handlers are the type of screen handlers used in vanilla.
 * They can automatically synchronize items and integer properties between the server and the client,
 * but they don't support having custom data sent in the opening packet.
 *
 * <p>This module adds <i>extended screen handlers</i> that can synchronize their own custom data
 * when they are opened, which can be useful for defining additional properties of a screen on the server.
 * For example, a mod can synchronize text that will show up as a label.
 *
 * <h2>Example</h2>
 * <pre>
 * {@code
 * // Creating the screen handler type
 * public static final ScreenHandlerType<OvenScreenHandler> OVEN = ScreenHandlerRegistry.registerSimple(new Identifier("my_mod", "oven"), OvenScreenHandler::new);
 *
 * // Screen handler class
 * public class OvenScreenHandler extends ScreenHandler {
 * 	public OvenScreenHandler(int syncId) {
 * 		super(MyScreenHandlers.OVEN, syncId);
 * 	}
 * }
 *
 * // Opening the screen
 * NamedScreenHandlerFactory factory = ...;
 * player.openHandledScreen(factory); // only works on ServerPlayerEntity instances
 * }
 * </pre>
 *
 * @see net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry registering screens for screen handlers
 */
public final class ScreenHandlerRegistry {
	private ScreenHandlerRegistry() {
	}

	/**
	 * Creates and registers a new {@code ScreenHandlerType} that creates client-sided screen handlers using the factory.
	 *
	 * @param id      the registry ID
	 * @param factory the client-sided screen handler factory
	 * @param <T>     the screen handler type
	 * @return the created type object
	 */
	public static <T extends ScreenHandler> ScreenHandlerType<T> registerSimple(Identifier id, SimpleClientHandlerFactory<T> factory) {
		// Wrap our factory in vanilla's factory; it will not be public for users.
		// noinspection Convert2MethodRef - Must be a lambda or else dedicated server will crash
		ScreenHandlerType<T> type = new ScreenHandlerType<>((syncId, inventory) -> factory.create(syncId, inventory));
		return Registry.register(Registry.SCREEN_HANDLER, id, type);
	}

	/**
	 * Creates and registers a new {@code ScreenHandlerType} that creates client-sided screen handlers with additional
	 * networked opening data.
	 *
	 * <p>These screen handlers must be opened with a {@link ExtendedScreenHandlerFactory}.
	 *
	 * @param id      the registry ID
	 * @param factory the client-sided screen handler factory
	 * @param <T>     the screen handler type
	 * @return the created type object
	 */
	public static <T extends ScreenHandler> ScreenHandlerType<T> registerExtended(Identifier id, ExtendedClientHandlerFactory<T> factory) {
		ScreenHandlerType<T> type = new ExtendedScreenHandlerType<>(factory);
		return Registry.register(Registry.SCREEN_HANDLER, id, type);
	}

	/**
	 * A factory for client-sided screen handler instances.
	 *
	 * @param <T> the screen handler type
	 */
	public interface SimpleClientHandlerFactory<T extends ScreenHandler> {
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
	 * with additional screen opening data.
	 *
	 * @param <T> the screen handler type
	 * @see ExtendedScreenHandlerFactory
	 */
	public interface ExtendedClientHandlerFactory<T extends ScreenHandler> {
		/**
		 * Creates a new client-sided screen handler with additional screen opening data.
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
