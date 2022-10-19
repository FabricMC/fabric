package net.fabricmc.fabric.mixin.registry.sync;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.class_7780;
import net.minecraft.util.registry.DynamicRegistryManager;

import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;

@Mixin(class_7780.class)
public class class_7780Mixin {
	@Shadow
	@Final
	private DynamicRegistryManager.Immutable field_40583;

	@Inject(method = "<init>(Ljava/util/List;Ljava/util/List;)V", at = @At("RETURN"))
	private void init(List list, List list2, CallbackInfo ci) {
		DynamicRegistrySetupCallback.EVENT.invoker().onRegistrySetup(this.field_40583);
	}
}
