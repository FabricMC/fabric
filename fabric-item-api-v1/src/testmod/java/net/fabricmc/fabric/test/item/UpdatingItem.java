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

package net.fabricmc.fabric.test.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class UpdatingItem extends Item {
	private static final EntityAttributeModifier PLUS_FIVE = new EntityAttributeModifier(
			ATTACK_DAMAGE_MODIFIER_ID, "updating item", 5, EntityAttributeModifier.Operation.ADDITION);

	private final boolean allowUpdateAnimation;

	public UpdatingItem(boolean allowUpdateAnimation) {
		super(new Settings().group(ItemGroup.MISC));
		this.allowUpdateAnimation = allowUpdateAnimation;
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		if (!world.isClient) {
			NbtCompound tag = stack.getOrCreateNbt();
			tag.putLong("ticks", tag.getLong("ticks")+1);
		}
	}

	@Override
	public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack originalStack, ItemStack updatedStack) {
		return allowUpdateAnimation;
	}

	@Override
	public boolean allowContinuingBlockBreaking(PlayerEntity player, ItemStack oldStack, ItemStack newStack) {
		return true; // set to false and you won't be able to break a block in survival with this item
	}

	// True for 15 seconds every 30 seconds
	private boolean isEnabled(ItemStack stack) {
		return !stack.hasNbt() || stack.getNbt().getLong("ticks") % 600 < 300;
	}

	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
		// Give + 5 attack damage for 15 seconds every 30 seconds.
		if (slot == EquipmentSlot.MAINHAND && isEnabled(stack)) {
			return ImmutableMultimap.of(EntityAttributes.GENERIC_ATTACK_DAMAGE, PLUS_FIVE);
		} else {
			return ImmutableMultimap.of();
		}
	}

	@Override
	public boolean isSuitableFor(ItemStack stack, BlockState state) {
		// Suitable for everything for 15 seconds every 30 seconds.
		return isEnabled(stack);
	}

	@Override
	public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
		return isEnabled(stack) ? 20 : super.getMiningSpeedMultiplier(stack, state);
	}
}
