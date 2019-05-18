package net.fabricmc.fabric.mixin.renderer.client;

import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugHud.class)
public class MixinDebugHud {
	@Inject(at = @At("RETURN"), method = "getLeftText")
	protected void getLeftText(CallbackInfoReturnable<List<String>> info) {
		if (RendererAccess.INSTANCE.hasRenderer()) {
			info.getReturnValue().add("[Fabric] Active renderer: " + RendererAccess.INSTANCE.getRenderer().getClass().getSimpleName());
		} else {
			info.getReturnValue().add("[Fabric] Active renderer: none (vanilla)");
		}
	}
}
