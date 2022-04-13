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

package net.fabricmc.fabric.mixin.item;

import java.util.UUID;

import com.google.common.collect.ImmutableMultimap;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;

@Mixin(ArmorItem.class)
public class ArmorItemMixin {
	@Shadow private static @Final UUID[] MODIFIERS;
	@Shadow protected @Final float knockbackResistance;

	/* Vanilla only adds a knockback resistance modifier to ArmorItems made of ArmorMaterials.NETHERITE. This mixin
	 * adds a knockback resistance modifier to any ArmorItem if knockbackResistance is > 0.0F.
	 */
	@ModifyVariable(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMultimap$Builder;build()Lcom/google/common/collect/ImmutableMultimap;"))
	private ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> fabric_knockbackResistance(ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder, ArmorMaterial material, EquipmentSlot slot) {
		// Vanilla handles netherite
		if (material != ArmorMaterials.NETHERITE && knockbackResistance > 0.0F) {
			builder.put(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, new EntityAttributeModifier(
					MODIFIERS[slot.getEntitySlotId()], "Armor knockback resistance",
					knockbackResistance, EntityAttributeModifier.Operation.ADDITION));
		}

		return builder;
	}
}
