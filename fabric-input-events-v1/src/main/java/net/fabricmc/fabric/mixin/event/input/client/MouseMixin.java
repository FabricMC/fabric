package net.fabricmc.fabric.mixin.event.input.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.fabric.impl.client.input.InputCallbacks;
import net.minecraft.client.Mouse;

@Mixin(Mouse.class)
public class MouseMixin {
    @Inject(method="method_22689(JDD)V", at=@At("HEAD"))
    void onMouseMove(long window, double dx, double dy, CallbackInfo ci) {
        InputCallbacks.onMouseMoved(window, dx, dy);
    }

    @Inject(method="method_22686(JIII)V", at=@At("HEAD"))
    void onMouseButton(long window, int button, int action, int modKeys, CallbackInfo ci) {
        InputCallbacks.onMouseButton(window, button, action, modKeys);
    }

    @Inject(method="method_22687(JDD)V", at=@At("HEAD"))
    void onMouseScrolled(long window, double dx, double dy, CallbackInfo ci) {
        InputCallbacks.onMouseScrolled(window, dx, dy);
    }

    @Inject(method="method_29615(JIJ)V", at=@At("HEAD"))
    void onFilesDropped(long window, int count, long names, CallbackInfo ci) {
        InputCallbacks.onFilesDropped(window, count, names);
    }
}
