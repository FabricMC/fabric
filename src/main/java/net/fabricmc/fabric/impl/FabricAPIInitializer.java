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

package net.fabricmc.fabric.impl;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.block.BlockAttackInteractionAware;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.fabricmc.loader.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.server.command.ServerCommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.ActionResult;

public class FabricAPIInitializer implements ModInitializer {
	@Override
	public void onInitialize() {
		AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
			BlockState state = world.getBlockState(pos);
			if (state instanceof BlockAttackInteractionAware) {
				if (((BlockAttackInteractionAware) state).onAttackInteraction(state, world, pos, player, hand, direction)) {
					return ActionResult.FAIL;
				}
			} else if (state.getBlock() instanceof BlockAttackInteractionAware) {
				if (((BlockAttackInteractionAware) state.getBlock()).onAttackInteraction(state, world, pos, player, hand, direction)) {
					return ActionResult.FAIL;
				}
			}

			return ActionResult.PASS;
		});
		
		CommandRegistry.INSTANCE.register(false, serverCommandSourceCommandDispatcher ->
			serverCommandSourceCommandDispatcher.register(ServerCommandManager
				.literal("fabric")
				.requires((serverCommandSource_1) -> { return serverCommandSource_1.hasPermissionLevel(2); })
				.then(ServerCommandManager.literal("mods")
				.executes(context -> {
					ServerCommandSource source = context.getSource();
					String modList = "";
					for (int i = 0; i < FabricLoader.INSTANCE.getMods().size(); i++) {
						modList = modList + "\n  " + TextFormat.GREEN + FabricLoader.INSTANCE.getMods().get(i).getInfo().getName() + TextFormat.YELLOW + " " + FabricLoader.INSTANCE.getMods().get(i).getInfo().getVersionString() + TextFormat.RESET + " " + new TranslatableTextComponent("fabric.command.by").getText() + " " + TextFormat.LIGHT_PURPLE + FabricLoader.INSTANCE.getMods().get(i).getInfo().getAuthors();
					}
					source.sendFeedback(new StringTextComponent(new TranslatableTextComponent("fabric.command.mods_loaded").getText() + modList), true);
					return 1;
		}))));
	}
}
