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

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

public class BlockEntityAdderItem extends Item {
	public static AttachmentType<Integer, BlockEntity> INT = AttachmentType.forBlockEntity(
			AttachmentsTestmod.id("int"),
			Integer.class,
			AttachmentType.Serializer.fromCodec(Codec.INT)
	);

	private final int delta;
	private final boolean client;

	public BlockEntityAdderItem(int delta, boolean client) {
		super(new Settings().group(ItemGroup.MISC));

		this.delta = delta;
		this.client = client;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		BlockEntity blockEntity = context.getWorld().getBlockEntity(context.getBlockPos());

		if (blockEntity != null) {
			if (context.getWorld().isClient() == client) {
				int currentValue = Objects.requireNonNullElse(INT.get(blockEntity), 0);
				int newValue = currentValue + delta;

				if (newValue == 0) {
					INT.remove(blockEntity);
				} else {
					INT.set(blockEntity, newValue);
				}

				// Remember to always call mark dirty manually if the changes need to be persisted!
				blockEntity.markDirty();

				context.getPlayer().sendMessage(Text.literal("New value in block entity: " + INT.get(blockEntity)), false);
			}

			return ActionResult.success(context.getWorld().isClient());
		}

		return ActionResult.PASS;
	}
}
