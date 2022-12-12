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

package net.fabricmc.fabric.mixin.itemgroup.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

import net.fabricmc.fabric.impl.client.itemgroup.CreativeGuiExtensions;
import net.fabricmc.fabric.impl.client.itemgroup.FabricCreativeGuiComponents;
import net.fabricmc.fabric.impl.itemgroup.FabricItemGroup;
import net.fabricmc.fabric.impl.itemgroup.ItemGroupHelper;

@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin<T extends ScreenHandler> extends AbstractInventoryScreen<T> implements CreativeGuiExtensions {
	public CreativeInventoryScreenMixin(T screenHandler, PlayerInventory playerInventory, Text text) {
		super(screenHandler, playerInventory, text);
	}

	@Shadow
	protected abstract void setSelectedTab(ItemGroup itemGroup_1);

	@Shadow
	private static ItemGroup selectedTab;

	// "static" matches selectedTab
	private static int fabric_currentPage = 0;

	@Override
	public void fabric_nextPage() {
		if (!fabric_hasGroupForPage(fabric_currentPage + 1)) {
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
		return ItemGroups.getGroupsToDisplay().size() > (ItemGroups.operatorEnabled ? 14 : 13);
	}

	@Override
	public boolean fabric_isButtonEnabled(FabricCreativeGuiComponents.Type type) {
		if (type == FabricCreativeGuiComponents.Type.NEXT) {
			return fabric_hasGroupForPage(fabric_currentPage + 1);
		}

		if (type == FabricCreativeGuiComponents.Type.PREVIOUS) {
			return fabric_currentPage != 0;
		}

		return false;
	}

	private void fabric_updateSelection() {
		ItemGroupHelper.sortedGroups.stream()
				.filter(this::fabric_isGroupVisible)
				.findFirst()
				.ifPresent(this::setSelectedTab);
	}

	@Inject(method = "init", at = @At("RETURN"))
	private void init(CallbackInfo info) {
		fabric_currentPage = fabric_getPage(selectedTab);

		int xpos = x + 170;
		int ypos = y + 4;

		addDrawableChild(new FabricCreativeGuiComponents.ItemGroupButtonWidget(xpos + 11, ypos, FabricCreativeGuiComponents.Type.NEXT, this));
		addDrawableChild(new FabricCreativeGuiComponents.ItemGroupButtonWidget(xpos, ypos, FabricCreativeGuiComponents.Type.PREVIOUS, this));
	}

	@Inject(method = "setSelectedTab", at = @At("HEAD"), cancellable = true)
	private void setSelectedTab(ItemGroup itemGroup, CallbackInfo info) {
		if (!fabric_isGroupVisible(itemGroup)) {
			info.cancel();
		}
	}

	@Inject(method = "renderTabTooltipIfHovered", at = @At("HEAD"), cancellable = true)
	private void renderTabTooltipIfHovered(MatrixStack matrixStack, ItemGroup itemGroup, int mx, int my, CallbackInfoReturnable<Boolean> info) {
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

	@Inject(method = "renderTabIcon", at = @At("HEAD"), cancellable = true)
	private void renderTabIcon(MatrixStack matrixStack, ItemGroup itemGroup, CallbackInfo info) {
		if (!fabric_isGroupVisible(itemGroup)) {
			info.cancel();
		}
	}

	private boolean fabric_isGroupVisible(ItemGroup itemGroup) {
		if (FabricCreativeGuiComponents.COMMON_GROUPS.contains(itemGroup)) {
			return true;
		}

		return fabric_currentPage == fabric_getPage(itemGroup);
	}

	private static int fabric_getPage(ItemGroup itemGroup) {
		final FabricItemGroup fabricItemGroup = (FabricItemGroup) itemGroup;
		return fabricItemGroup.getPage();
	}

	private static boolean fabric_hasGroupForPage(int page) {
		return ItemGroups.getGroupsToDisplay().stream()
				.anyMatch(itemGroup -> fabric_getPage(itemGroup) == page);
	}

	@Override
	public int fabric_currentPage() {
		return fabric_currentPage;
	}
}
