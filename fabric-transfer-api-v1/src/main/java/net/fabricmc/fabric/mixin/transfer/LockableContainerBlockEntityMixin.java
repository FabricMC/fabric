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

package net.fabricmc.fabric.mixin.transfer;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.item.ItemStack;

import net.fabricmc.fabric.impl.transfer.item.SpecialLogicInventory;

/**
 * Defer markDirty until the outer transaction close callback when setStack is called from an inventory wrapper.
 */
@Mixin(LockableContainerBlockEntity.class)
public class LockableContainerBlockEntityMixin implements SpecialLogicInventory {
	@Unique
	private boolean fabric_suppressSpecialLogic = false;

	@WrapOperation(
			at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/LockableContainerBlockEntity;markDirty()V"),
			method = "setStack(ILnet/minecraft/item/ItemStack;)V"
	)
	public void fabric_redirectMarkDirty(LockableContainerBlockEntity instance, Operation<Void> original) {
		if (!fabric_suppressSpecialLogic) {
			original.call(instance);
		}
	}

	@Override
	public void fabric_setSuppress(boolean suppress) {
		fabric_suppressSpecialLogic = suppress;
	}

	@Override
	public void fabric_onFinalCommit(int slot, ItemStack oldStack, ItemStack newStack) {
	}
}
