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

package net.fabricmc.fabric.test.modelevents;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

// This test must pass without the tool attribute API present.
// It has its own handlers for mining levels, which might "hide" this module
// not working on its own.
public final class ModelEventsTest implements ModInitializer {
	private static final String ID = "fabric-model-events-api-v1-testmod";
	private static final Logger LOGGER = LoggerFactory.getLogger(ModelEventsTest.class);

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTED.register(server -> test());
	}

	private static void test() {
		List<AssertionError> errors = new ArrayList<>();

		if (errors.isEmpty()) {
			LOGGER.info("Model Events tests passed!");
		} else {
			AssertionError error = new AssertionError("Model Events tests failed!");
			errors.forEach(error::addSuppressed);
			throw error;
		}
	}

	private static void test(List<AssertionError> errors, Runnable runnable) {
		try {
			runnable.run();
		} catch (AssertionError e) {
			errors.add(e);
		}
	}
}
