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

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.attachment.v1.AttachmentSerializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

public class EntityAdderItem extends Item {
	public static AttachmentType<Integer, Entity> INT = AttachmentType.forEntity(
			AttachmentsTestmod.id("int"),
			Integer.class,
			AttachmentSerializer.fromCodec(Codec.INT)
	);

	private final int delta;
	private final boolean client;

	public EntityAdderItem(int delta, boolean client) {
		super(new Settings().group(ItemGroup.MISC));

		this.delta = delta;
		this.client = client;
	}

	@Override
	public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
		if (!user.isSneaking()) {
			// Apply to other entity if not sneaking
			applyToEntity(user.world, user, entity);
			return ActionResult.success(user.world.isClient());
		}

		return super.useOnEntity(stack, user, entity, hand);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if (user.isSneaking()) {
			// Apply on self if sneaking
			applyToEntity(world, user, user);
			return TypedActionResult.success(user.getStackInHand(hand), world.isClient());
		}

		return super.use(world, user, hand);
	}

	private void applyToEntity(World world, PlayerEntity user, Entity target) {
		if (world.isClient() == client) {
			int currentValue = Objects.requireNonNullElse(INT.get(target), 0);
			int newValue = currentValue + delta;

			if (newValue == 0) {
				INT.remove(target);
			} else {
				INT.set(target, newValue);
			}

			// No need to call mark dirty for entities, they always get saved
			// TODO: check this claim!!!

			user.sendMessage(Text.literal("New value in entity: " + INT.get(target)), false);
		}
	}
}
