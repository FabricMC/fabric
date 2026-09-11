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

package net.fabricmc.fabric.api.menu.v1;

import java.util.Objects;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

/**
 * A {@link MenuType} for an extended menus that
 * synchronizes additional data to the client when it is opened.
 *
 * <p>Extended menus can be opened using
 * {@link net.minecraft.world.entity.player.Player#openMenu(MenuProvider)
 * Player.openMenu} with an
 * {@link ExtendedMenuProvider}.
 *
 * <h2>Example</h2>
 * <pre>
 * {@code
 * // Data record holding the additional information synced to the client
 * public record OvenData(int burnTime) {
 * 	public static final StreamCodec<RegistryFriendlyByteBuf, OvenData> STREAM_CODEC = StreamCodec.composite(
 * 		ByteBufCodecs.VAR_INT,
 * 		OvenData::burnTime,
 * 		OvenData::new
 * 	);
 * }
 *
 * // Creating and registering the type
 * public static final ExtendedMenuType<OvenMenu, OvenData> OVEN_MENU = Registry.register(
 * 	BuiltInRegistries.MENU,
 * 	Identifier.fromNamespaceAndPath("modid", "oven"),
 * 	new ExtendedMenuType<>(OvenMenu::new, OvenData.STREAM_CODEC)
 * );
 *
 * // Note: remember to also register the screen using vanilla's MenuScreens!
 *
 * // Menu class
 * public class OvenMenu extends AbstractContainerMenu {
 * 	private final int burnTime;
 *
 * 	// Used on the client, constructed from the synced OvenData
 * 	public OvenMenu(int containerId, Inventory playerInventory, OvenData data) {
 * 		this(containerId, playerInventory, data.burnTime());
 * 	}
 *
 * 	// Used on the server, constructed directly from the block entity
 * 	public OvenMenu(int containerId, Inventory playerInventory, int burnTime) {
 * 		super(MyMenus.OVEN_MENU, containerId);
 * 		this.burnTime = burnTime;
 * 	}
 * }
 *
 * // Menu provider, typically implemented by a block entity
 * public class OvenBlockEntity extends BlockEntity implements ExtendedMenuProvider<OvenData> {
 * 	private int burnTime;
 *
 * 	@Override
 * 	public OvenData getScreenOpeningData(ServerPlayer player) {
 * 		return new OvenData(burnTime);
 * 	}
 *
 * 	@Override
 * 	public Component getDisplayName() {
 * 		return Component.translatable("container.modid.oven");
 * 	}
 *
 * 	@Override
 * 	public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
 * 		return new OvenMenu(containerId, playerInventory, burnTime);
 * 	}
 * }
 *
 * // Opening the extended menu
 * player.openMenu(ovenBlockEntity); // only works on ServerPlayer instances
 * }
 * </pre>
 *
 * @param <T> the type of menu created by this type
 * @param <D> the type of the data
 */
public class ExtendedMenuType<T extends AbstractContainerMenu, D> extends MenuType<T> {
	private final ExtendedFactory<T, D> factory;
	private final StreamCodec<? super RegistryFriendlyByteBuf, D> streamCodec;

	/**
	 * Constructs an extended menu type.
	 *
	 * @param factory the menu factory used for {@link #create(int, Inventory, Object)}
	 */
	public ExtendedMenuType(ExtendedFactory<T, D> factory, StreamCodec<? super RegistryFriendlyByteBuf, D> streamCodec) {
		super(null, FeatureFlags.VANILLA_SET);
		this.factory = Objects.requireNonNull(factory, "menu factory cannot be null");
		this.streamCodec = Objects.requireNonNull(streamCodec, "stream codec cannot be null");
	}

	/**
	 * @throws UnsupportedOperationException always; use {@link #create(int, Inventory, Object)}
	 * @deprecated Use {@link #create(int, Inventory, Object)} instead.
	 */
	@Deprecated
	@Override
	public final T create(int containerId, Inventory inventory) {
		throw new UnsupportedOperationException("Use ExtendedMenuType.create(int, Inventory, FriendlyByteBuf)!");
	}

	/**
	 * Creates a new menu using the extra opening data.
	 *
	 * @param containerId    the container ID
	 * @param inventory the player inventory
	 * @param data      the synced opening data
	 * @return the created menu
	 */
	public T create(int containerId, Inventory inventory, D data) {
		return factory.create(containerId, inventory, data);
	}

	/**
	 * @return the stream codec for serializing the data of this menu
	 */
	public StreamCodec<? super RegistryFriendlyByteBuf, D> getStreamCodec() {
		return streamCodec;
	}

	/**
	 * A factory for creating menu instances from
	 * additional opening data.
	 * This is primarily used on the client, but can be called on the
	 * server too.
	 *
	 * @param <T> the type of menus created
	 * @param <D> the type of the data
	 * @see #create(int, Inventory, Object)
	 */
	@FunctionalInterface
	public interface ExtendedFactory<T extends AbstractContainerMenu, D> {
		/**
		 * Creates a new menu with additional screen opening data.
		 *
		 * @param containerId    the container ID
		 * @param inventory the player inventory
		 * @param data      the synced data
		 * @return the created menu
		 */
		T create(int containerId, Inventory inventory, D data);
	}
}
