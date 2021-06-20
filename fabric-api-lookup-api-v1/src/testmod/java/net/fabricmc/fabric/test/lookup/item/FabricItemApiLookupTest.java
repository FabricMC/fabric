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

package net.fabricmc.fabric.test.lookup.item;

import static net.fabricmc.fabric.test.lookup.FabricApiLookupTest.ensureException;

import net.minecraft.block.Material;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ToolItem;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.test.lookup.FabricApiLookupTest;

public class FabricItemApiLookupTest {
	// Use /setblock to place it in the world.
	public static final InspectorBlock INSPECTOR = new InspectorBlock(FabricBlockSettings.of(Material.METAL));
	public static final InspectableItem HELLO_ITEM = new InspectableItem("Hello Fabric API tester!");

	public static void onInitialize() {
		Registry.register(Registry.BLOCK, new Identifier(FabricApiLookupTest.MOD_ID, "inspector"), INSPECTOR);
		Registry.register(Registry.ITEM, new Identifier(FabricApiLookupTest.MOD_ID, "hello"), HELLO_ITEM);

		// Diamonds and diamond blocks can be inspected and will also print their name.
		Inspectable.LOOKUP.registerForItems((stack, ignored) -> () -> {
			if (stack.hasCustomName()) {
				return stack.getName();
			} else {
				return new LiteralText("Unnamed gem.");
			}
		}, Items.DIAMOND, Items.DIAMOND_BLOCK);
		// Test registerSelf
		Inspectable.LOOKUP.registerSelf(HELLO_ITEM);
		// Tools report their mining level
		Inspectable.LOOKUP.registerFallback((stack, ignored) -> {
			Item item = stack.getItem();

			if (item instanceof ToolItem) {
				return () -> new LiteralText("Tool mining level: " + ((ToolItem) item).getMaterial().getMiningLevel());
			} else {
				return null;
			}
		});

		testSelfRegistration();
	}

	private static void testSelfRegistration() {
		ensureException(() -> {
			Inspectable.LOOKUP.registerSelf(Items.WATER_BUCKET);
		}, "The ItemApiLookup should have prevented self-registration of incompatible items.");
	}
}
