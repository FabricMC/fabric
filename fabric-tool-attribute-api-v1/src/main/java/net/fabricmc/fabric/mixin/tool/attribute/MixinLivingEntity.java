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

package net.fabricmc.fabric.mixin.tool.attribute;

import com.google.common.collect.Multimap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;
import net.fabricmc.fabric.impl.tool.attribute.AttributeManager;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {
	public MixinLivingEntity(EntityType<?> type, World world) {
		super(type, world);
	}

	/**
	 * @author B0undarybreaker
	 * @reason get entity attribute modifiers for dynamic tools
	 */
	@Redirect(method = "method_30129", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getAttributeModifiers(Lnet/minecraft/entity/EquipmentSlot;)Lcom/google/common/collect/Multimap;"))
	public Multimap<EntityAttribute, EntityAttributeModifier> actTickModifiers(ItemStack stack, EquipmentSlot slot) {
		return actModifiers(stack, slot, (LivingEntity) (Object) this);
	}

	private static Multimap<EntityAttribute, EntityAttributeModifier> actModifiers(ItemStack stack, EquipmentSlot slot, LivingEntity user) {
		Multimap<EntityAttribute, EntityAttributeModifier> original = stack.getAttributeModifiers(slot);

		if (stack.getItem() instanceof DynamicAttributeTool) {
			DynamicAttributeTool tool = (DynamicAttributeTool) stack.getItem();
			return (AttributeManager.mergeAttributes(original, tool.getDynamicModifiers(slot, stack, user)));
		}

		return original;
	}
}
