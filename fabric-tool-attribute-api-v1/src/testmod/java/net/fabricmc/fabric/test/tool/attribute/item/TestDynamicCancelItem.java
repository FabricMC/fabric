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

import java.util.UUID;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;

public class TestDynamicCancelItem extends Item implements DynamicAttributeTool {
	public static final UUID TEST_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");

	public TestDynamicCancelItem(Settings settings) {
		super(settings);
	}

	@Override
	public Multimap<EntityAttribute, EntityAttributeModifier> getDynamicModifiers(EquipmentSlot slot, ItemStack stack, @Nullable LivingEntity user) {
		if (slot.equals(EquipmentSlot.MAINHAND)) {
			ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
			builder.put(EntityAttributes.GENERIC_MOVEMENT_SPEED, new EntityAttributeModifier(TEST_UUID, "TEST", -1.0, EntityAttributeModifier.Operation.ADDITION));
			builder.put(EntityAttributes.GENERIC_MOVEMENT_SPEED, new EntityAttributeModifier(TEST_UUID, "TEST", 1.0, EntityAttributeModifier.Operation.ADDITION));
			return builder.build();
		} else {
			return EMPTY;
		}
	}
}
