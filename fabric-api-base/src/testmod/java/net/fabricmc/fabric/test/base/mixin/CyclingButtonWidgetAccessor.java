package net.fabricmc.fabric.test.base.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.text.Text;

@Mixin(CyclingButtonWidget.class)
public interface CyclingButtonWidgetAccessor {
	@Accessor
	Text getOptionText();
}
