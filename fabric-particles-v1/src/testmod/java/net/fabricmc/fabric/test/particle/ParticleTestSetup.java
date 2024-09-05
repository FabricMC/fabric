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

package net.fabricmc.fabric.test.particle;

import com.mojang.brigadier.Command;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public final class ParticleTestSetup implements ModInitializer {
	// The dust particles of this block are always tinted (default).
	public static final RegistryKey<Block> ALWAYS_TINTED_KEY = block("always_tinted");
	public static final Block ALWAYS_TINTED = new ParticleTintTestBlock(AbstractBlock.Settings.create().breakInstantly().registryKey(ALWAYS_TINTED_KEY), 0xFF00FF);
	// The dust particles of this block are only tinted when the block is broken over water.
	public static final RegistryKey<Block> TINTED_OVER_WATER_KEY = block("tinted_over_water");
	public static final Block TINTED_OVER_WATER = new ParticleTintTestBlock(AbstractBlock.Settings.create().breakInstantly().registryKey(TINTED_OVER_WATER_KEY), 0xFFFF00);
	// The dust particles of this block are never tinted.
	public static final RegistryKey<Block> NEVER_TINTED_KEY = block("never_tinted");
	public static final Block NEVER_TINTED = new ParticleTintTestBlock(AbstractBlock.Settings.create().breakInstantly().registryKey(NEVER_TINTED_KEY), 0x00FFFF);

	@Override
	public void onInitialize() {
		registerBlock(ALWAYS_TINTED_KEY, ALWAYS_TINTED);
		registerBlock(TINTED_OVER_WATER_KEY, TINTED_OVER_WATER);
		registerBlock(NEVER_TINTED_KEY, NEVER_TINTED);

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("addparticletestblocks").executes(context -> {
				PlayerInventory inventory = context.getSource().getPlayerOrThrow().getInventory();
				inventory.offerOrDrop(new ItemStack(ALWAYS_TINTED));
				inventory.offerOrDrop(new ItemStack(TINTED_OVER_WATER));
				inventory.offerOrDrop(new ItemStack(NEVER_TINTED));
				return Command.SINGLE_SUCCESS;
			}));
		});
	}

	private static RegistryKey<Block> block(String path) {
		return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of("fabric-particles-v1-testmod", path));
	}

	private static void registerBlock(RegistryKey<Block> key, Block block) {
		Registry.register(Registries.BLOCK, key, block);
		Registry.register(Registries.ITEM, key.getValue(), new BlockItem(block, new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, key.getValue()))));
	}
}
