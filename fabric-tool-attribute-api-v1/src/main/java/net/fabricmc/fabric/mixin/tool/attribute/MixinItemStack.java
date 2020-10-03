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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;
import net.fabricmc.fabric.api.tool.attribute.v1.ToolManager;
import net.fabricmc.fabric.impl.tool.attribute.AttributeManager;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {
	@Shadow
	public abstract Item getItem();

	@Inject(at = @At("RETURN"), method = "isEffectiveOn", cancellable = true)
	public void isEffectiveOn(BlockState state, CallbackInfoReturnable<Boolean> info) {
		info.setReturnValue(ToolManager.handleIsEffectiveOnIgnoresVanilla(state, (ItemStack) (Object) this, null, info.getReturnValueZ()));
	}

	@Inject(at = @At("RETURN"), method = "getMiningSpeedMultiplier", cancellable = true)
	public void getMiningSpeedMultiplier(BlockState state, CallbackInfoReturnable<Float> info) {
		float customSpeed = ToolManager.handleBreakingSpeedIgnoresVanilla(state, (ItemStack) (Object) this, null);

		if (info.getReturnValueF() < customSpeed) {
			info.setReturnValue(customSpeed);
		}
	}

	@Inject(at = @At("RETURN"), method = "getAttributeModifiers", cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	public void getAttributeModifiers(EquipmentSlot slot, CallbackInfoReturnable<Multimap<EntityAttribute, EntityAttributeModifier>> info, Multimap<EntityAttribute, EntityAttributeModifier> multimap) {
		ItemStack stack = (ItemStack) (Object) this;

		if (stack.getItem() instanceof DynamicAttributeTool) {
			DynamicAttributeTool holder = (DynamicAttributeTool) stack.getItem();
			Multimap<EntityAttribute, EntityAttributeModifier> ret = AttributeManager.mergeAttributes(multimap, (holder).getDynamicModifiers(slot, stack, null));
			info.setReturnValue(ret);
		}
	}
}
