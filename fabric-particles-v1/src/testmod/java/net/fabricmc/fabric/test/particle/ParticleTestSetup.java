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
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public final class ParticleTestSetup implements ModInitializer {
	// The dust particles of this block are always tinted (default).
	public static final Block ALWAYS_TINTED = new ParticleTintTestBlock(AbstractBlock.Settings.of(Material.STONE).breakInstantly(), 0xFF00FF);
	// The dust particles of this block are only tinted when the block is broken over water.
	public static final Block TINTED_OVER_WATER = new ParticleTintTestBlock(AbstractBlock.Settings.of(Material.STONE).breakInstantly(), 0xFFFF00);
	// The dust particles of this block are never tinted.
	public static final Block NEVER_TINTED = new ParticleTintTestBlock(AbstractBlock.Settings.of(Material.STONE).breakInstantly(), 0x00FFFF);

	@Override
	public void onInitialize() {
		registerBlock("always_tinted", ALWAYS_TINTED);
		registerBlock("tinted_over_water", TINTED_OVER_WATER);
		registerBlock("never_tinted", NEVER_TINTED);

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("addparticletestblocks").executes(context -> {
				PlayerInventory inventory = context.getSource().getPlayer().getInventory();
				inventory.offerOrDrop(new ItemStack(ALWAYS_TINTED));
				inventory.offerOrDrop(new ItemStack(TINTED_OVER_WATER));
				inventory.offerOrDrop(new ItemStack(NEVER_TINTED));
				return Command.SINGLE_SUCCESS;
			}));
		});
	}

	private static void registerBlock(String path, Block block) {
		Identifier id = new Identifier("fabric-particles-v1-testmod", path);
		Registry.register(Registry.BLOCK, id, block);
		Registry.register(Registry.ITEM, id, new BlockItem(block, new Item.Settings()));
	}
}
