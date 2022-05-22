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

package net.fabricmc.fabric.mixin.item.group.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.item.ItemGroup;

import net.fabricmc.fabric.impl.item.group.FabricCreativeGuiComponents;

@Mixin(ItemGroup.class)
public abstract class MixinItemGroup {
	@Shadow
	public abstract int getIndex();

	@Shadow
	public abstract boolean isTopRow();

	@Inject(method = "isTopRow", cancellable = true, at = @At("HEAD"))
	private void isTopRow(CallbackInfoReturnable<Boolean> info) {
		if (getIndex() > 11) {
			info.setReturnValue((getIndex() - 12) % (12 - FabricCreativeGuiComponents.COMMON_GROUPS.size()) < 4);
		}
	}

	@Inject(method = "getColumn", cancellable = true, at = @At("HEAD"))
	private void getColumn(CallbackInfoReturnable<Integer> info) {
		if (getIndex() > 11) {
			if (isTopRow()) {
				info.setReturnValue((getIndex() - 12) % (12 - FabricCreativeGuiComponents.COMMON_GROUPS.size()));
			} else {
				info.setReturnValue((getIndex() - 12) % (12 - FabricCreativeGuiComponents.COMMON_GROUPS.size()) - 4);
			}
		}
	}
}
