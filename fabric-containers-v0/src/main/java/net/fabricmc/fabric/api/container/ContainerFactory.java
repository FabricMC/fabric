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

package net.fabricmc.fabric.api.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

@FunctionalInterface
public interface ContainerFactory<T> {
	/**
	 * Creates the new object.
	 *
	 * @param syncId     The container synchronization ID.
	 * @param identifier the Identifier is the name that was used when registering the factory
	 * @param player     the player that is opening the gui/container
	 * @param buf        the buffer contains the same data that was provided with {@link net.fabricmc.fabric.api.container.ContainerProviderRegistry#openContainer}
	 * @return the new gui or container
	 */
	T create(int syncId, Identifier identifier, PlayerEntity player, PacketByteBuf buf);
}
