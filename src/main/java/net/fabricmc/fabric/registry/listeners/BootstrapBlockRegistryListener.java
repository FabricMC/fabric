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

package net.fabricmc.fabric.registry.listeners;

import com.google.common.collect.UnmodifiableIterator;
import net.fabricmc.fabric.registry.ExtendedIdList;
import net.fabricmc.fabric.registry.RegistryListener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BootstrapBlockRegistryListener implements RegistryListener<Block> {
	@Override
	public void beforeCleared(Registry<Block> registry) {
		((ExtendedIdList) Block.BLOCKSTATE_ID_LIST).clear();
	}

	@Override
	public void beforeRegistered(Registry<Block> registry, int id, Identifier identifier, Block object, boolean isNew) {
		// refer net.minecraft.block.Blocks
		for (BlockState state : object.getStateFactory().getStates()) {
			state.method_11590();
			Block.BLOCKSTATE_ID_LIST.method_10205(state);
		}

		object.getDropTableId();
	}
}
