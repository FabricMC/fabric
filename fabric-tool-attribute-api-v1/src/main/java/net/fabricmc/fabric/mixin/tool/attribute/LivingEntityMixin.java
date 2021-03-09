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

import java.util.Map;

import com.google.common.collect.Multimap;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.impl.tool.attribute.ItemStackContext;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Nullable
	@Unique private ItemStack stackContext = null;
	@Nullable
	@Unique private EquipmentSlot slotContext = null;

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/AttributeContainer;removeModifiers(Lcom/google/common/collect/Multimap;)V"), method = "method_30129", locals = LocalCapture.CAPTURE_FAILHARD)
	private void storeRemoveStackContext(CallbackInfoReturnable<Map> cir, Map map, EquipmentSlot[] var2, int var3, int var4, EquipmentSlot equipmentSlot, ItemStack oldStack, ItemStack newStack) {
		stackContext = oldStack;
		slotContext = equipmentSlot;
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/AttributeContainer;removeModifiers(Lcom/google/common/collect/Multimap;)V"), method = "method_30129")
	private void setupRemoveModifierContext(AttributeContainer attributeContainer, Multimap<EntityAttribute, EntityAttributeModifier> oldModifiers) {
		((ItemStackContext) (Object) stackContext).fabricToolAttributes_setContext((LivingEntity) (Object) this);
		Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers = stackContext.getAttributeModifiers(slotContext);
		((ItemStackContext) (Object) stackContext).fabricToolAttributes_setContext(null);
		attributeContainer.removeModifiers(attributeModifiers);
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/AttributeContainer;addTemporaryModifiers(Lcom/google/common/collect/Multimap;)V"), method = "method_30129", locals = LocalCapture.CAPTURE_FAILHARD)
	private void storeAddStackContext(CallbackInfoReturnable<Map> cir, Map map, EquipmentSlot[] var2, int var3, int var4, EquipmentSlot equipmentSlot, ItemStack oldStack, ItemStack newStack) {
		stackContext = newStack;
		slotContext = equipmentSlot;
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/AttributeContainer;addTemporaryModifiers(Lcom/google/common/collect/Multimap;)V"), method = "method_30129")
	private void setupAddModifierContext(AttributeContainer attributeContainer, Multimap<EntityAttribute, EntityAttributeModifier> oldModifiers) {
		((ItemStackContext) (Object) stackContext).fabricToolAttributes_setContext((LivingEntity) (Object) this);
		Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers = stackContext.getAttributeModifiers(slotContext);
		((ItemStackContext) (Object) stackContext).fabricToolAttributes_setContext(null);
		attributeContainer.addTemporaryModifiers(attributeModifiers);
	}
}
