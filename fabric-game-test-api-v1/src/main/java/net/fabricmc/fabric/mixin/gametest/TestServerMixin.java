package net.fabricmc.fabric.mixin.gametest;

import net.minecraft.test.TestServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TestServer.class)
public abstract class TestServerMixin {

	@Inject(method = "isDedicated", at = @At("HEAD"), cancellable = true)
	public void isDedicated(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(true);
	}
}
