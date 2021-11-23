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

package net.fabricmc.fabric.test.tool.attribute.item;

import com.google.common.collect.Multimap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;

import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;

public class TestDynamicContextItem extends Item implements DynamicAttributeTool {
	private static final Logger LOGGER = LogManager.getLogger();

	public TestDynamicContextItem() {
		super(new Settings().group(ItemGroup.TOOLS).maxCount(1));
	}

	@Override
	public int getMiningLevel(Tag<Item> tag, BlockState state, ItemStack stack, @Nullable LivingEntity user) {
		checkUser(user);
		return 4;
	}

	@Override
	public float getMiningSpeedMultiplier(Tag<Item> tag, BlockState state, ItemStack stack, @Nullable LivingEntity user) {
		checkUser(user);
		return 3;
	}

	@Override
	public float postProcessMiningSpeed(Tag<Item> tag, BlockState state, ItemStack stack, @Nullable LivingEntity user, float currentSpeed, boolean isEffective) {
		checkUser(user);
		return DynamicAttributeTool.super.postProcessMiningSpeed(tag, state, stack, user, currentSpeed, isEffective);
	}

	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getDynamicModifiers(EquipmentSlot slot, ItemStack stack, @Nullable LivingEntity user) {
		checkUser(user);
		return DynamicAttributeTool.super.getDynamicModifiers(slot, stack, user);
	}

	private static void checkUser(@Nullable LivingEntity user) {
		if (user == null) {
			Throwable stacktraceCapture = new Throwable();
			LOGGER.warn("Dynamic Pickaxe Item found missing user context. Stacktrace:", stacktraceCapture);
		}
	}
}
