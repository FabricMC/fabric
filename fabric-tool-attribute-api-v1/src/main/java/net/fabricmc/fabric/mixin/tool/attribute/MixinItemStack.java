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
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;
import net.fabricmc.fabric.api.tool.attribute.v1.ToolManager;
import net.fabricmc.fabric.api.util.TriState;
import net.fabricmc.fabric.impl.tool.attribute.AttributeManager;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {
	@Shadow
	public abstract Item getItem();

	@Inject(at = @At("HEAD"), method = "isEffectiveOn", cancellable = true)
	public void isEffectiveOn(BlockState state, CallbackInfoReturnable<Boolean> info) {
		TriState triState = ToolManager.handleIsEffectiveOn((ItemStack) (Object) this, state, null);

		if (triState != TriState.DEFAULT) {
			info.setReturnValue(triState.get());
			info.cancel();
		}
	}

	@Inject(at = @At("HEAD"), method = "getMiningSpeed", cancellable = true)
	public void getMiningSpeed(BlockState state, CallbackInfoReturnable<Float> info) {
		TriState triState = ToolManager.handleIsEffectiveOn((ItemStack) (Object) this, state, null);

		if (triState == TriState.TRUE) {
			info.setReturnValue(ToolManager.handleBreakingSpeed((ItemStack) (Object) this, state, null));
		} else if (triState == TriState.FALSE) {
			info.setReturnValue(1f);
		}
	}

	@Inject(at = @At("RETURN"), method = "getAttributeModifiers", cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	public void getAttributeModifiers(EquipmentSlot slot, CallbackInfoReturnable<Multimap<String, EntityAttributeModifier>> info, Multimap<String, EntityAttributeModifier> multimap) {
		ItemStack stack = (ItemStack) (Object) this;

		if (stack.getItem() instanceof DynamicAttributeTool) {
			DynamicAttributeTool holder = (DynamicAttributeTool) stack.getItem();
			Multimap<String, EntityAttributeModifier> ret = AttributeManager.mergeAttributes(multimap, (holder).getDynamicModifiers(slot, stack, null));
			info.setReturnValue(ret);
		}
	}
}
