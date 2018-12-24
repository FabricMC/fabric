/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.mixin.client.itemgroup;

import net.fabricmc.fabric.client.itemgroup.ItemGroupExtensions;
import net.minecraft.item.ItemGroup;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemGroup.class)
public abstract class MixinItemGroup implements ItemGroupExtensions {

	@Shadow
	@Final
	@Mutable
	public static ItemGroup[] GROUPS;

	@Shadow
	public abstract int getId();

	@Shadow
	public abstract boolean isTopRow();

	@Shadow
	@Final
	private int id;

	@Override
	public void fabric_expandArray() {
		ItemGroup[] tempGroups = GROUPS;
		GROUPS = new ItemGroup[GROUPS.length + 1];
		for (ItemGroup group : tempGroups) {
			GROUPS[group.getId()] = group;
		}
	}

	@Inject(method = "isTopRow", cancellable = true, at = @At("HEAD"))
	private void isTopRow(CallbackInfoReturnable<Boolean> info) {
		if (getId() > 11) {
			info.setReturnValue((id - 12) % 9 < 4);
		}
	}

	@Inject(method = "getColumn", cancellable = true, at = @At("HEAD"))
	private void getColumn(CallbackInfoReturnable<Integer> info) {
		if (getId() > 11) {
			if (isTopRow()) {
				info.setReturnValue((id - 12) % 9);
			} else {
				info.setReturnValue((id - 12) % 9 - 4);
			}

		}
	}
}
