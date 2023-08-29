package net.fabricmc.fabric.mixin.client.keybinding;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.client.gui.screen.option.ControlsListWidget;

@Mixin(ControlsListWidget.KeyBindingEntry.class)
public class KeyBindingEntryMixin {
	@ModifyConstant(method = "update", constant = @Constant(stringValue = ", "))
	private String makeConflictTextMultiline(String constant) {
		return "\n";
	}

	@ModifyConstant(method = "update", constant = @Constant(stringValue = "controls.keybinds.duplicateKeybinds"))
	private String replaceConflictText(String constant) {
		return "fabric.keybinding.conflicts";
	}
}
