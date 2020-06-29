package net.fabricmc.fabric.mixin.resource.loader;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.pack.AbstractPackScreen;
import net.minecraft.client.gui.screen.pack.PackListWidget;
import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;

import net.fabricmc.fabric.impl.resource.loader.ModResourcePackCreator;

@Mixin(AbstractPackScreen.class)
public class MixinAbstractPackScreen {
	@Inject(method = "method_29672", at = @At("HEAD"), cancellable = true)
	private void addPackEntry(PackListWidget packListWidget, ResourcePackOrganizer.Pack pack, CallbackInfo info) {
		if (pack.getSource() == ModResourcePackCreator.RESOURCE_PACK_SOURCE) {
			info.cancel();
		}
	}
}
