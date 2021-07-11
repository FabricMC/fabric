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

package net.fabricmc.fabric.test.tag.extension;

import net.minecraft.server.command.CommandManager;
import net.minecraft.tag.RequiredTagList;
import net.minecraft.tag.RequiredTagListRegistry;
import net.minecraft.tag.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

public class TagExtensionTest implements ModInitializer {
	@Override
	public void onInitialize() {
		RequiredTagList<Biome> biomeTagList = RequiredTagListRegistry.register(Registry.BIOME_KEY, "tags/biomes");
		// crash test
		RequiredTagListRegistry.register(Registry.BIOME_KEY, "tags/biomes");
		Tag<Biome> biomes = biomeTagList.add("fabric-tag-extensions-v0-testmod:example");

		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
				dispatcher.register(CommandManager.literal("biome_tag_test").executes(context -> {
					biomes.values().forEach(biome -> {
						Identifier id = context.getSource().getRegistryManager().get(Registry.BIOME_KEY).getId(biome);
						context.getSource().sendFeedback(new LiteralText(id.toString()), false);
					});
					return 1;
				})));
	}
}
