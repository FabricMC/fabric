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

package net.fabricmc.fabric.api.item.v1;

import com.google.common.collect.Multimap;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Stack-aware attribute modifier callback for foreign items.
 * Instead of using Mixin to change attribute modifiers in items not in your mod,
 * you can use this event instead, either checking the Item itself or using a tag.
 * This event provides you with a guaranteed mutable map you can put attribute modifiers in.
 * Do not use for your own Item classes; see {@link FabricItem#getAttributeModifiers} instead.
 * For example, the following code modifies a Diamond Helmet to give you five extra hearts when wearing.
 *
 * <pre>
 * {@code
 * ModifyItemAttributeModifiersCallback.EVENT.register((stack, slot, attributeModifiers) -> {
 * 	if (stack.isOf(Items.DIAMOND_HELMET) && slot.getEntitySlotId() == HEAD_SLOT_ID) {
 * 		attributeModifiers.put(EntityAttributes.GENERIC_MAX_HEALTH, MODIFIER);
 * 	}
 * });
 * }
 * </pre>
 */
@FunctionalInterface
public interface ModifyItemAttributeModifiersCallback {
	void modifyAttributeModifiers(ItemStack stack, EquipmentSlot slot, Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers);

	Event<ModifyItemAttributeModifiersCallback> EVENT = EventFactory.createArrayBacked(
			ModifyItemAttributeModifiersCallback.class,
			callbacks -> (stack, slot, attributeModifiers) -> {
				for (ModifyItemAttributeModifiersCallback callback : callbacks) {
					callback.modifyAttributeModifiers(stack, slot, attributeModifiers);
				}
			}
	);
}
