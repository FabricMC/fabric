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

package net.fabricmc.fabric.test.attachment.ingame;

import java.util.Objects;

import com.mojang.serialization.Codec;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

import net.fabricmc.fabric.api.attachment.v1.AttachmentSerializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

public class ChunkAdderItem extends Item {
	public static AttachmentType<Integer, WorldChunk> INT = AttachmentType.forChunk(
			AttachmentsTestmod.id("int"),
			Integer.class,
			AttachmentSerializer.fromCodec(Codec.INT)
	);

	private final int delta;
	private final boolean client;

	public ChunkAdderItem(int delta, boolean client) {
		super(new Settings().group(ItemGroup.MISC));

		this.delta = delta;
		this.client = client;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		WorldChunk chunk = world.getWorldChunk(new BlockPos(user.getEyePos()));

		if (world.isClient() == client) {
			int currentValue = Objects.requireNonNullElse(INT.get(chunk), 0);
			int newValue = currentValue + delta;

			if (newValue == 0) {
				INT.remove(chunk);
			} else {
				INT.set(chunk, newValue);
			}

			// Remember to always call setNeedsSaving manually if the changes need to be persisted!
			chunk.setNeedsSaving(true);

			user.sendMessage(Text.literal("New value in chunk: " + INT.get(chunk)), false);
		}

		return TypedActionResult.success(user.getStackInHand(hand), world.isClient());
	}
}
