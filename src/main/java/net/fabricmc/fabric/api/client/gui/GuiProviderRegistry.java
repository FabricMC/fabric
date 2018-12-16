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

package net.fabricmc.fabric.api.client.gui;

import net.fabricmc.fabric.api.container.ContainerFactory;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.impl.client.gui.GuiProviderImpl;
import net.minecraft.client.gui.ContainerGui;
import net.minecraft.util.Identifier;

public interface GuiProviderRegistry {

	GuiProviderRegistry INSTANCE = GuiProviderImpl.INSTANCE;

	/**
	 *
	 * Register a gui factory, this should only be done on the client side and not on the dedicated server.
	 *
	 * @param identifier a shared identifier, this identifier should also be used to register a container using {@link ContainerProviderRegistry}
	 * @param factory the gui factory, this should return a new {@link ContainerGui}
	 */
	void registerFactory(Identifier identifier, ContainerFactory<ContainerGui> factory);

}
