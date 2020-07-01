package net.fabricmc.fabric.mixin.screen;

import java.util.function.Function;

import com.mojang.datafixers.util.Function4;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.storage.LevelStorage;

import net.fabricmc.fabric.api.client.screen.v1.ScreenContext;
import net.fabricmc.fabric.api.client.screen.v1.ScreenTickCallback;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
	@Shadow
	public Screen currentScreen;

	// Should be caught by "Screen#wrapScreenError" if anything fails
	@Inject(method = "method_1572", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void onTickScreen(CallbackInfo ci) {
		ScreenTickCallback.EVENT.invoker().onTick((MinecraftClient) (Object) this, this.currentScreen, (ScreenContext) this.currentScreen);
	}

	// This is the odd screen that isn't ticked by the main tick loop, so we fire events for this screen.
	@Inject(method = "startIntegratedServer(Ljava/lang/String;Lnet/minecraft/util/registry/RegistryTracker$Modifiable;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;ZLnet/minecraft/client/MinecraftClient$WorldLoadAction;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;render(Z)V"))
	private void onTickLoadingScreen(String worldName, RegistryTracker.Modifiable registryTracker, Function<LevelStorage.Session, DataPackSettings> function, Function4<LevelStorage.Session, RegistryTracker.Modifiable, ResourceManager, DataPackSettings, SaveProperties> function4, boolean safeMode, @Coerce Object worldLoadAction, CallbackInfo ci) {
		final Screen currentScreen = this.currentScreen;
		ScreenTickCallback.EVENT.invoker().onTick((MinecraftClient) (Object) this, currentScreen, (ScreenContext) currentScreen);
	}
}
