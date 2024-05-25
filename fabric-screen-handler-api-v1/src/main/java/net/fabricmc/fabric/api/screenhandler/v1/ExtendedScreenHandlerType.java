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

import java.util.Objects;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

/**
 * A {@link ScreenHandlerType} for an extended screen handler that
 * synchronizes additional data to the client when it is opened.
 *
 * <p>Extended screen handlers can be opened using
 * {@link net.minecraft.entity.player.PlayerEntity#openHandledScreen(NamedScreenHandlerFactory)
 * PlayerEntity.openHandledScreen} with an
 * {@link ExtendedScreenHandlerFactory}.
 *
 * <h2>Example</h2>
 * <pre>
 * {@code
 * // Data class
 * public record OvenData(String label) {
 *     public static final PacketCodec<RegistryByteBuf, OvenData> PACKET_CODEC = PacketCodec.tuple(
 *     	PacketCodecs.STRING,
 *     	OvenData::label,
 *     	OvenData::new
 *     );
 * }
 *
 * // Creating and registering the type
 * public static final ExtendedScreenHandlerType<OvenScreenHandler> OVEN =
 * 	new ExtendedScreenHandlerType((syncId, inventory, data) -> ..., OvenData.PACKET_CODEC);
 * Registry.register(Registry.SCREEN_HANDLER, Identifier.of(...), OVEN);
 *
 * // Note: remember to also register the screen using vanilla's HandledScreens!
 *
 * // Screen handler class
 * public class OvenScreenHandler extends ScreenHandler {
 * 	public OvenScreenHandler(int syncId) {
 * 		super(MyScreenHandlers.OVEN, syncId);
 * 	}
 * }
 *
 * // Opening the extended screen handler
 * var factory = new ExtendedScreenHandlerFactory() {
 * 	...
 * };
 * player.openHandlerScreen(factory); // only works on ServerPlayerEntity instances
 * }
 * </pre>
 *
 * @param <T> the type of screen handler created by this type
 * @param <D> the type of the data
 */
public class ExtendedScreenHandlerType<T extends ScreenHandler, D> extends ScreenHandlerType<T> {
	private final ExtendedFactory<T, D> factory;
	private final PacketCodec<? super RegistryByteBuf, D> packetCodec;

	/**
	 * Constructs an extended screen handler type.
	 *
	 * @param factory the screen handler factory used for {@link #create(int, PlayerInventory, Object)}
	 */
	public ExtendedScreenHandlerType(ExtendedFactory<T, D> factory, PacketCodec<? super RegistryByteBuf, D> packetCodec) {
		super(null, FeatureFlags.VANILLA_FEATURES);
		this.factory = Objects.requireNonNull(factory, "screen handler factory cannot be null");
		this.packetCodec = Objects.requireNonNull(packetCodec, "packet codec cannot be null");
	}

	/**
	 * @throws UnsupportedOperationException always; use {@link #create(int, PlayerInventory, Object)}
	 * @deprecated Use {@link #create(int, PlayerInventory, Object)} instead.
	 */
	@Deprecated
	@Override
	public final T create(int syncId, PlayerInventory inventory) {
		throw new UnsupportedOperationException("Use ExtendedScreenHandlerType.create(int, PlayerInventory, PacketByteBuf)!");
	}

	/**
	 * Creates a new screen handler using the extra opening data.
	 *
	 * @param syncId    the sync ID
	 * @param inventory the player inventory
	 * @param data      the synced opening data
	 * @return the created screen handler
	 */
	public T create(int syncId, PlayerInventory inventory, D data) {
		return factory.create(syncId, inventory, data);
	}

	/**
	 * @return the packet codec for serializing the data of this screen handler
	 */
	public PacketCodec<? super RegistryByteBuf, D> getPacketCodec() {
		return packetCodec;
	}

	/**
	 * A factory for creating screen handler instances from
	 * additional opening data.
	 * This is primarily used on the client, but can be called on the
	 * server too.
	 *
	 * @param <T> the type of screen handlers created
	 * @param <D> the type of the data
	 * @see #create(int, PlayerInventory, Object)
	 */
	@FunctionalInterface
	public interface ExtendedFactory<T extends ScreenHandler, D> {
		/**
		 * Creates a new screen handler with additional screen opening data.
		 *
		 * @param syncId    the synchronization ID
		 * @param inventory the player inventory
		 * @param data      the synced data
		 * @return the created screen handler
		 */
		T create(int syncId, PlayerInventory inventory, D data);
	}
}
