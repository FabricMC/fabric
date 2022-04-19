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

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.entity.LootableContainerBlockEntity;

import net.fabricmc.fabric.impl.transfer.TransferApiImpl;

/**
 * Defer markDirty until the outer transaction close callback when setStack is called from an inventory wrapper.
 */
@Mixin(LootableContainerBlockEntity.class)
public class LootableContainerBlockEntityMixin {
	@Redirect(
			at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/LootableContainerBlockEntity;markDirty()V"),
			method = "setStack(ILnet/minecraft/item/ItemStack;)V"
	)
	public void fabric_redirectMarkDirty(LootableContainerBlockEntity self) {
		if (TransferApiImpl.SUPPRESS_SPECIAL_LOGIC.get() == null) {
			self.markDirty();
		}
	}
}
