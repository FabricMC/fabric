package net.fabricmc.fabric.mixin.resource.loader;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackManager;

import net.fabricmc.fabric.impl.resource.loader.ModResourcePackUtil;

@Mixin(ResourcePackManager.class)
public class MixinResourcePackManagerClient {
	@Inject(method = "method_29211", at = @At("RETURN"), cancellable = true)
	public void method_29211(CallbackInfoReturnable<List<ResourcePack>> infoReturnable) {
		List<ResourcePack> list = new ArrayList<>(infoReturnable.getReturnValue());
		ModResourcePackUtil.modifyResourcePackList(list);
		infoReturnable.setReturnValue(list);
	}
}
