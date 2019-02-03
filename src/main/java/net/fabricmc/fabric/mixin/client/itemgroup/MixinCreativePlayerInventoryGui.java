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

import net.fabricmc.fabric.client.itemgroup.CreativeGuiExtensions;
import net.fabricmc.fabric.client.itemgroup.FabricCreativeGuiComponents;
import net.minecraft.client.gui.ingame.AbstractPlayerInventoryScreen;
import net.minecraft.client.gui.ingame.CreativePlayerInventoryScreen;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.TextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreativePlayerInventoryScreen.class)
public abstract class MixinCreativePlayerInventoryGui extends AbstractPlayerInventoryScreen implements CreativeGuiExtensions {

	public MixinCreativePlayerInventoryGui(Container container_1, PlayerInventory playerInventory_1, TextComponent textComponent_1) {
		super(container_1, playerInventory_1, textComponent_1);
	}

	@Shadow
	protected abstract void setSelectedTab(ItemGroup itemGroup_1);

	@Shadow
	public abstract int method_2469(); /* XXX getSelectedTab XXX */

	// "static" matches selectedTab
	private static int fabric_currentPage = 0;

	private int fabric_getPageOffset(int page) {
		switch (page) {
			case 0:
				return 0;
			case 1:
				return 12;
			default:
				return 12 + ((12 - FabricCreativeGuiComponents.COMMON_GROUPS.size()) * (page - 1));
		}
	}

	private int fabric_getOffsetPage(int offset) {
		if (offset < 12) {
			return 0;
		} else {
			return 1 + ((offset - 12) / (12 - FabricCreativeGuiComponents.COMMON_GROUPS.size()));
		}
	}

	@Override
	public void fabric_nextPage() {
		if (fabric_getPageOffset(fabric_currentPage + 1) > ItemGroup.GROUPS.length) {
			return;
		}
		fabric_currentPage++;
		fabric_updateSelection();
	}

	@Override
	public void fabric_previousPage() {
		if (fabric_currentPage == 0) {
			return;
		}
		fabric_currentPage--;
		fabric_updateSelection();
	}

	@Override
	public boolean fabric_isButtonVisible(FabricCreativeGuiComponents.Type type) {
		return ItemGroup.GROUPS.length != 12;
	}

	@Override
	public boolean fabric_isButtonEnabled(FabricCreativeGuiComponents.Type type) {
		if (type == FabricCreativeGuiComponents.Type.NEXT) {
			return !(fabric_getPageOffset(fabric_currentPage + 1) > ItemGroup.GROUPS.length);
		}
		if (type == FabricCreativeGuiComponents.Type.PREVIOUS) {
			return fabric_currentPage != 0;
		}
		return false;
	}

	private void fabric_updateSelection() {
		int minPos = fabric_getPageOffset(fabric_currentPage);
		int maxPos = fabric_getPageOffset(fabric_currentPage + 1) - 1;
		int curPos = method_2469();

		if (curPos < minPos || curPos > maxPos) {
			setSelectedTab(ItemGroup.GROUPS[fabric_getPageOffset(fabric_currentPage)]);
		}
	}

	@Inject(method = "onInitialized", at = @At("RETURN"))
	private void onInitialized(CallbackInfo info) {
		fabric_updateSelection();

		int xpos = left + 170;
		int ypos = top + 4;

		addButton(new FabricCreativeGuiComponents.ItemGroupButtonWidget(1001, xpos + 10, ypos, FabricCreativeGuiComponents.Type.NEXT, this));
		addButton(new FabricCreativeGuiComponents.ItemGroupButtonWidget(1002, xpos, ypos, FabricCreativeGuiComponents.Type.PREVIOUS, this));

	}

	@Inject(method = "setSelectedTab", at = @At("HEAD"), cancellable = true)
	private void setSelectedTab(ItemGroup itemGroup, CallbackInfo info) {
		if (!fabric_isGroupVisible(itemGroup)) {
			info.cancel();
		}
	}

	@Inject(method = "method_2471", at = @At("HEAD"), cancellable = true)
	private void method_2471(ItemGroup itemGroup, int mx, int my, CallbackInfoReturnable<Boolean> info) {
		if (!fabric_isGroupVisible(itemGroup)) {
			info.setReturnValue(false);
		}
	}

	@Inject(method = "isClickInTab", at = @At("HEAD"), cancellable = true)
	private void isClickInTab(ItemGroup itemGroup, double mx, double my, CallbackInfoReturnable<Boolean> info) {
		if (!fabric_isGroupVisible(itemGroup)) {
			info.setReturnValue(false);
		}
	}

	@Inject(method = "method_2468", at = @At("HEAD"), cancellable = true)
	private void method_2468(ItemGroup itemGroup, CallbackInfo info) {
		if (!fabric_isGroupVisible(itemGroup)) {
			info.cancel();
		}
	}

	private boolean fabric_isGroupVisible(ItemGroup itemGroup) {
		if (FabricCreativeGuiComponents.COMMON_GROUPS.contains(itemGroup)) {
			return true;
		}
		return fabric_currentPage == fabric_getOffsetPage(itemGroup.getId());

	}

	@Override
	public int fabric_currentPage() {
		return fabric_currentPage;
	}
}
