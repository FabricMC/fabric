package net.fabricmc.fabric.mixin.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.test.item.CustomSettingExtension;
import net.fabricmc.fabric.test.item.FabricItemSettingsTests;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public class ItemMixin implements CustomSettingExtension {
	@Unique
	private String testStringFromCustomSetting;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void onConstruct(Item.Settings settings, CallbackInfo ci) {
		if (settings instanceof FabricItemSettings) {
			this.testStringFromCustomSetting = FabricItemSettingsTests.CUSTOM_DATA_TEST.getValue(settings);
		}
	}

	@Override
	public String getTestString() {
		return this.testStringFromCustomSetting;
	}
}
