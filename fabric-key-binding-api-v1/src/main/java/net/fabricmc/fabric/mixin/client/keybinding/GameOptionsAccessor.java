package net.fabricmc.fabric.mixin.client.keybinding;

import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameOptions.class)
public interface GameOptionsAccessor {
	@Accessor("keysAll")
	void setKeyBindings(KeyBinding[] keyBindings);
}
