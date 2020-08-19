package net.fabricmc.fabric.mixin.client.keybinding;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;

@Mixin(GameOptions.class)
public interface GameOptionsAccessor {
	@Accessor("keysAll")
	void setKeyBindings(KeyBinding[] keyBindings);
}
