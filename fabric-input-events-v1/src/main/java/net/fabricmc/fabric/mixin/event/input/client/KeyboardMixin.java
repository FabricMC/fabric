package net.fabricmc.fabric.mixin.event.input.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.fabric.impl.client.input.InputCallbacks;
import net.minecraft.client.Keyboard;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Inject(method="method_22678(JIIII)V", at=@At("HEAD"))
    void onKey(long handle, int code, int scancode, int action, int mods, CallbackInfo ci) {
        InputCallbacks.onKey(handle, code, scancode, action, mods);
    }
    @Inject(method="method_22677(JII)V", at=@At("HEAD"))
    void onChar(long handle, int codepoint, int mods, CallbackInfo ci) {
        InputCallbacks.onChar(handle, codepoint, mods);
    }
}
