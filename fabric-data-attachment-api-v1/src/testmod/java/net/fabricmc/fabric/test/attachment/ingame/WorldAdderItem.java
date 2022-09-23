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
import net.minecraft.world.World;

import net.fabricmc.fabric.api.attachment.v1.AttachmentSerializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

public class WorldAdderItem extends Item {
	public static AttachmentType<Integer, World> INT = AttachmentType.forWorld(
			AttachmentsTestmod.id("int"),
			Integer.class,
			AttachmentSerializer.fromCodec(Codec.INT)
	);

	private final int delta;
	private final boolean client;

	public WorldAdderItem(int delta, boolean client) {
		super(new Settings().group(ItemGroup.MISC));

		this.delta = delta;
		this.client = client;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (world.isClient() == client) {
			int currentValue = Objects.requireNonNullElse(INT.get(world), 0);
			int newValue = currentValue + delta;

			if (newValue == 0) {
				INT.remove(world);
			} else {
				INT.set(world, newValue);
			}

			// No need to call markDirty for world attachments - this is for sure since we override isDirty in the persistent state!

			user.sendMessage(Text.literal("New value in world: " + INT.get(world)), false);
		}

		return TypedActionResult.success(user.getStackInHand(hand), world.isClient());
	}
}
