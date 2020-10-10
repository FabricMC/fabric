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

package net.fabricmc.fabric.impl.screenhandler;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;

public final class ExtendedScreenHandlerType<T extends ScreenHandler> extends ScreenHandlerType<T> {
	private final ScreenHandlerRegistry.ExtendedClientHandlerFactory<T> factory;

	public ExtendedScreenHandlerType(ScreenHandlerRegistry.ExtendedClientHandlerFactory<T> factory) {
		super(null);
		this.factory = factory;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public T create(int syncId, PlayerInventory inventory) {
		throw new UnsupportedOperationException("Use ExtendedScreenHandlerType.create(int, PlayerInventory, PacketByteBuf)!");
	}

	@Environment(EnvType.CLIENT)
	public T create(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
		return factory.create(syncId, inventory, buf);
	}
}
