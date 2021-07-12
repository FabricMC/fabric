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

import static net.minecraft.server.command.CommandManager.literal;

import java.util.Map;

import net.minecraft.tag.RequiredTagList;
import net.minecraft.tag.RequiredTagListRegistry;
import net.minecraft.tag.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.tag.TagRegistry;

public class TagExtensionTest implements ModInitializer {
	@Override
	public void onInitialize() {
		RequiredTagList<Biome> biomeTagList = RequiredTagListRegistry.register(Registry.BIOME_KEY, "tags/biomes");
		// Multiple registration test
		RequiredTagListRegistry.register(Registry.BIOME_KEY, "tags/biomes");

		// Creating biome tag via RequiredTagList#add
		Tag<Biome> biomesAdd = biomeTagList.add("fabric-tag-extensions-v0-testmod:example_add");
		// Creating biome tag via TagRegistry#create
		Tag<Biome> biomesTagRegistry = TagRegistry.create(new Identifier("fabric-tag-extensions-v0-testmod:example_tag_registry"), biomeTagList::getGroup);

		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(literal("biome_tag_test")
				.then(literal("RequiredTagList.add").executes(context -> {
					biomesAdd.values().forEach(biome -> {
						Identifier id = context.getSource().getRegistryManager().get(Registry.BIOME_KEY).getId(biome);
						context.getSource().sendFeedback(new LiteralText(id.toString()), false);
					});
					return 1;
				}))
				.then(literal("TagRegistry.create").executes(context -> {
					biomesTagRegistry.values().forEach(biome -> {
						Identifier id = context.getSource().getRegistryManager().get(Registry.BIOME_KEY).getId(biome);
						context.getSource().sendFeedback(new LiteralText(id.toString()), false);
					});
					return 1;
				}))
				.then(literal("list_all").executes(context -> {
					Map<Identifier, Tag<Biome>> tags = context.getSource().getServer().getTagManager().getOrCreateTagGroup(Registry.BIOME_KEY).getTags();
					tags.forEach((tagId, tag) -> {
						LiteralText text = new LiteralText(tagId.toString() + ":");
						tag.values().forEach(biome -> {
							Identifier biomeId = context.getSource().getRegistryManager().get(Registry.BIOME_KEY).getId(biome);
							text.append(" " + biomeId.toString());
						});
						context.getSource().sendFeedback(text, false);
					});
					return 1;
				}))));
	}
}
