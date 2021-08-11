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
import java.util.Optional;

import net.minecraft.tag.Tag;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.tag.TagFactory;

public class TagExtensionTest implements ModInitializer {
	static final TagFactory<Biome> BIOME_TAGS = TagFactory.of(Registry.BIOME_KEY, "tags/biomes");
	static final Tag<Biome> FACTORY_TEST = BIOME_TAGS.create(new Identifier("fabric-tag-extensions-v0-testmod:factory_test"));

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(literal("biome_tag_test")
				.then(literal("factory").executes(context -> {
					FACTORY_TEST.values().forEach(biome -> {
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
							Optional<RegistryKey<Biome>> biomeKey = context.getSource().getRegistryManager().get(Registry.BIOME_KEY).getKey(biome);
							biomeKey.ifPresent(key -> text.append(" " + key.getValue()));
						});
						context.getSource().sendFeedback(text, false);
					});
					return 1;
				}))));
	}
}
