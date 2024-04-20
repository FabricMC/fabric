package net.fabricmc.fabric.mixin.resource.loader.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.resource.ClientDataPackManager;
import net.minecraft.resource.ResourcePackManager;

import net.fabricmc.fabric.impl.resource.loader.ModResourcePackUtil;

@Mixin(ClientDataPackManager.class)
public class ClientDataPackManagerMixin {
	@Redirect(method = "<init>", at= @At(value = "INVOKE", target = "Lnet/minecraft/resource/VanillaDataPackProvider;createClientManager()Lnet/minecraft/resource/ResourcePackManager;"))
	public ResourcePackManager createClientManager(){
		return ModResourcePackUtil.createClientManager();
	}
}
