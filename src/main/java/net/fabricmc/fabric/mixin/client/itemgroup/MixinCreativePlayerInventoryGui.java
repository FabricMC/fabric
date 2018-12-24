package net.fabricmc.fabric.mixin.client.itemgroup;

import net.fabricmc.fabric.client.itemgroup.CreativeGuiExtensions;
import net.fabricmc.fabric.client.itemgroup.FabricItemGroupUtils;
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
	public void fabric_NextPage() {
		if((currentPage + 1) * 12 > ItemGroup.GROUPS.length){
			return;
		}
		currentPage ++;
		updateSelection();
	}

	@Override
	public void fabric_PreviousPage() {
		if(currentPage == 0){
			return;
		}
		currentPage--;
		updateSelection();
	}

	@Override
	public boolean fabric_isButtonVisible(FabricItemGroupUtils.Type type) {
		return ItemGroup.GROUPS.length != 12;
	}

	@Override
	public boolean fabric_isButtonEnabled(FabricItemGroupUtils.Type type) {
		if(type == FabricItemGroupUtils.Type.NEXT){
			return !((currentPage + 1) * 12 > ItemGroup.GROUPS.length);
		}
		if(type == FabricItemGroupUtils.Type.PREVIOUS){
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

		addButton(new FabricItemGroupUtils.FabricItemGroupButtonWidget(1001, xpos, ypos, FabricItemGroupUtils.Type.PREVIOUS, this));
		addButton(new FabricItemGroupUtils.FabricItemGroupButtonWidget(1001, xpos + 10, ypos, FabricItemGroupUtils.Type.NEXT, this));
	}

	@Inject(method = "setSelectedTab", at = @At("HEAD"), cancellable = true)
	private void setSelectedTab(ItemGroup itemGroup, CallbackInfo info) {
		if(!isGroupVisable(itemGroup)){
			info.cancel();
		}
	}

	@Inject(method = "method_2471", at = @At("HEAD"), cancellable = true)
	private void method_2471(ItemGroup itemGroup, int mx, int my, CallbackInfoReturnable<Boolean> info){
		if(!isGroupVisable(itemGroup)){
			info.setReturnValue(false);
		}
	}

	@Inject(method = "isClickInTab", at = @At("HEAD"), cancellable = true)
	private void isClickInTab(ItemGroup itemGroup, double mx, double my, CallbackInfoReturnable<Boolean> info){
		if(!isGroupVisable(itemGroup)){
			info.setReturnValue(false);
		}
	}

	@Inject(method = "method_2468", at = @At("HEAD"), cancellable = true)
	private void method_2468(ItemGroup itemGroup, CallbackInfo info){
		if(!isGroupVisable(itemGroup)){
			info.cancel();
		}
	}

	private boolean isGroupVisable(ItemGroup itemGroup){
		if(itemGroup == ItemGroup.SEARCH || itemGroup == ItemGroup.INVENTORY){
			return true;
		}
		if(itemGroup.getId() < 12){
			return currentPage == 0;
		}
		int page = (int) Math.floor((itemGroup.getId() - 12) / 10);
		return currentPage == page + 1;

	}


}
