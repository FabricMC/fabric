package net.fabricmc.fabric.mixin.event.interaction.client;

import net.minecraft.client.option.KeyBinding;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyBinding.class)
public interface InteractionEventsKeyBindingAccessor {
	@Accessor("timesPressed")
	int fabric_getTimesPressed();
}
