package net.fabricmc.fabric.mixin.event.input.client;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;

@Mixin(KeyBinding.class)
public interface KeyBindingMixin {
	@Accessor("keyToBindings")
	public static Map<InputUtil.Key, KeyBinding> fabric_getKeyToBindings() {
		throw new AssertionError();
	}

	@Accessor("boundKey")
	public Key fabric_getBoundKey();
}
