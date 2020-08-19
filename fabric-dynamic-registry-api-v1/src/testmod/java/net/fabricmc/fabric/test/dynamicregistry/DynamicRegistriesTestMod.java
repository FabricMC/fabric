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

package net.fabricmc.fabric.test.dynamicregistry;

import java.util.Map;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Lifecycle;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.test.dynamicregistry.tater.Tater;

public class DynamicRegistriesTestMod implements ModInitializer {
	private static final Identifier TATER_ID = new Identifier("fabric-dynamic-registry-api-v1-testmod", "tater");
	public static final RegistryKey<? extends Registry<Tater>> TATER_KEY = RegistryKey.ofRegistry(TATER_ID);

	public static final Identifier DEFAULT_TATER_ID = new Identifier("fabric-dynamic-registry-api-v1-testmod", "tiny_potato");
	public static final Tater DEFAULT_TATER = new Tater("Tiny Potato");

	public static SimpleRegistry<Tater> TATER_REGISTRY = new SimpleRegistry<>(TATER_KEY, Lifecycle.stable());

	@Override
	public void onInitialize() {
		Registry.register(TATER_REGISTRY, DEFAULT_TATER_ID, DEFAULT_TATER);
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			dispatcher.register(CommandManager.literal("fabric_dynamic_registries_test").executes(this::executeTestCommand));
		});
	}

	private int executeTestCommand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		Registry<Tater> registry = context.getSource().getMinecraftServer().getRegistryManager().get(TATER_KEY);

		Text header = new LiteralText("Found " + registry.getEntries().size() + " taters:").formatted(Formatting.GRAY);
		context.getSource().sendFeedback(header, false);

		for (Map.Entry<RegistryKey<Tater>, Tater> entry : registry.getEntries()) {
			Identifier id = entry.getKey().getValue();
			Tater tater = entry.getValue();

			context.getSource().sendFeedback(new LiteralText("- ID:   " + id), false);
			context.getSource().sendFeedback(new LiteralText("  Name: " + tater.getName()), false);
		}

		return 1;
	}
}
