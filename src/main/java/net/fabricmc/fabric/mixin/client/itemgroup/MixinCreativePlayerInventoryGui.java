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
import net.minecraft.client.gui.ingame.AbstractPlayerInventoryGui;
import net.minecraft.client.gui.ingame.CreativePlayerInventoryGui;
import net.minecraft.container.Container;
import net.minecraft.item.ItemGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreativePlayerInventoryGui.class)
public abstract class MixinCreativePlayerInventoryGui extends AbstractPlayerInventoryGui implements CreativeGuiExtensions {

	@Shadow private static int selectedTab;

	@Shadow protected abstract void setSelectedTab(ItemGroup itemGroup_1);

	private int currentPage = 0;

	public MixinCreativePlayerInventoryGui(Container container_1) {
		super(container_1);
	}

	@Override
	public void fabric_nextPage() {
		if((currentPage + 1) * 12 > ItemGroup.GROUPS.length){
			return;
		}
		currentPage ++;
		updateSelection();
	}

	@Override
	public void fabric_previousPage() {
		if(currentPage == 0){
			return;
		}
		currentPage--;
		updateSelection();
	}

	@Override
	public boolean fabric_isButtonVisible(FabricCreativeGuiComponents.Type type) {
		return ItemGroup.GROUPS.length != 12;
	}

	@Override
	public boolean fabric_isButtonEnabled(FabricCreativeGuiComponents.Type type) {
		if(type == FabricCreativeGuiComponents.Type.NEXT){
			return !((currentPage + 1) * 12 > ItemGroup.GROUPS.length);
		}
		if(type == FabricCreativeGuiComponents.Type.PREVIOUS){
			return currentPage != 0;
		}
		return false;
	}

	private void updateSelection(){
		if(currentPage == 0){
			selectedTab = 0;
		} else {
			selectedTab = 12 + 10 * (currentPage -1);
		}
		setSelectedTab(ItemGroup.GROUPS[selectedTab]);
	}

	@Inject(method = "onInitialized", at = @At("RETURN"))
	private void onInitialized(CallbackInfo info){
		updateSelection();

		int xpos = left + 171;
		int ypos = top + 5;

		addButton(new FabricCreativeGuiComponents.ItemGroupButtonWidget(1001, xpos, ypos, FabricCreativeGuiComponents.Type.PREVIOUS, this));
		addButton(new FabricCreativeGuiComponents.ItemGroupButtonWidget(1001, xpos + 10, ypos, FabricCreativeGuiComponents.Type.NEXT, this));
	}

	@Inject(method = "setSelectedTab", at = @At("HEAD"), cancellable = true)
	private void setSelectedTab(ItemGroup itemGroup, CallbackInfo info) {
		if(!isGroupVisible(itemGroup)){
			info.cancel();
		}
	}

	@Inject(method = "method_2471", at = @At("HEAD"), cancellable = true)
	private void method_2471(ItemGroup itemGroup, int mx, int my, CallbackInfoReturnable<Boolean> info){
		if(!isGroupVisible(itemGroup)){
			info.setReturnValue(false);
		}
	}

	@Inject(method = "isClickInTab", at = @At("HEAD"), cancellable = true)
	private void isClickInTab(ItemGroup itemGroup, double mx, double my, CallbackInfoReturnable<Boolean> info){
		if(!isGroupVisible(itemGroup)){
			info.setReturnValue(false);
		}
	}

	@Inject(method = "method_2468", at = @At("HEAD"), cancellable = true)
	private void method_2468(ItemGroup itemGroup, CallbackInfo info){
		if(!isGroupVisible(itemGroup)){
			info.cancel();
		}
	}

	private boolean isGroupVisible(ItemGroup itemGroup){
		if(FabricCreativeGuiComponents.COMMON_GROUPS.contains(itemGroup)){
			return true;
		}
		if(itemGroup.getId() < 12){
			return currentPage == 0;
		}
		int page = (int) Math.floor((itemGroup.getId() - 12) / (12 - FabricCreativeGuiComponents.COMMON_GROUPS.size()));
		return currentPage == page + 1;

	}


}
