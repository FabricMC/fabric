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

package net.fabricmc.fabric.test.attachment.client;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.test.attachment.AttachmentTestMod;

public class AttachmentTestModClient implements ClientModInitializer {
	private static AbstractClientPlayerEntity parseClientPlayer(FabricClientCommandSource source, String name) throws CommandSyntaxException {
		if (name.equals("@s")) {
			return source.getPlayer();
		} else {
			for (AbstractClientPlayerEntity player : source.getWorld().getPlayers()) {
				if (name.equals(player.getName().getLiteralString())) {
					return player;
				}
			}

			throw EntityArgumentType.PLAYER_NOT_FOUND_EXCEPTION.create();
		}
	}

	@Override
	public void onInitializeClient() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(literal("attachment_test").then(argument("target", StringArgumentType.word()).executes(
					context -> {
						AbstractClientPlayerEntity player = parseClientPlayer(
								context.getSource(),
								StringArgumentType.getString(context, "target")
						);
						context.getSource().sendFeedback(
								Text.literal("Attachments for player %s:".formatted(player.getName().getLiteralString()))
						);
						boolean attAll = player.getAttachedOrCreate(AttachmentTestMod.SYNCED_WITH_ALL);
						context.getSource().sendFeedback(
								Text.literal("Synced-with-all attachment: %s".formatted(attAll)).withColor(
										attAll ? Colors.GREEN : Colors.WHITE
								)
						);
						boolean attTarget = player.getAttachedOrCreate(AttachmentTestMod.SYNCED_WITH_TARGET);
						context.getSource().sendFeedback(
								Text.literal("Synced-with-target attachment: %s".formatted(attTarget)).withColor(
										attTarget ? player == MinecraftClient.getInstance().player ? Colors.GREEN : Colors.RED : Colors.WHITE
								)
						);
						boolean attOther = player.getAttachedOrCreate(AttachmentTestMod.SYNCED_EXCEPT_TARGET);
						context.getSource().sendFeedback(
								Text.literal("Synced-with-non-targets attachment: %s".formatted(attOther)).withColor(
										attOther ? player != MinecraftClient.getInstance().player ? Colors.GREEN : Colors.RED : Colors.WHITE
								)
						);
						boolean attCustom = player.getAttachedOrCreate(AttachmentTestMod.SYNCED_CUSTOM_RULE);
						context.getSource().sendFeedback(
								Text.literal("Synced-with-creative attachment: %s".formatted(attCustom)).withColor(
										attCustom ? player.isCreative() ? Colors.GREEN : Colors.RED : Colors.WHITE
								)
						);
						return 1;
					}))
			);
		});
	}
}
