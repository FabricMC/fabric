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

	@Shadow @Final @Mutable
	public static ItemGroup[] GROUPS;

	@Shadow public abstract int getId();

	@Override
	public void fabric_expandArray() {
		ItemGroup[] tempGroups = GROUPS;
		GROUPS = new ItemGroup[GROUPS.length + 1];
		for(ItemGroup group : tempGroups){
			GROUPS[group.getId()] = group;
		}
	}

	@Inject(method = "isTopRow", cancellable = true, at = @At("HEAD"))
	private void isTopRow(CallbackInfoReturnable<Boolean> info){
		if(getId() > 11){
			//We show 10 tabs per page, where as vanilla actually has 12
			info.setReturnValue((getId() - 12) % 10 < 5);
		}
	}

	@Inject(method = "getColumn", cancellable = true, at = @At("HEAD"))
	private void getColumn(CallbackInfoReturnable<Integer> info){
		if(getId() > 11){
			info.setReturnValue(((getId() - 12 % 10) % 5));
		}
	}
}
