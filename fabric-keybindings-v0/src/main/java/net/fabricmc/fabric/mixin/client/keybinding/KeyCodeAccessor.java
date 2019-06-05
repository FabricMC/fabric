package net.fabricmc.fabric.mixin.client.keybinding;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyBinding.class)
public interface KeyCodeAccessor
{
	@Accessor("keyCode")
	InputUtil.KeyCode fabric_getConfiguredKeyCode();
}
