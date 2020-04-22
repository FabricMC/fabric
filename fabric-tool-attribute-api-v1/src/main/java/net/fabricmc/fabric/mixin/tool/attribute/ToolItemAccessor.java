package net.fabricmc.fabric.mixin.tool.attribute;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;

@Mixin(ToolItem.class)
public interface ToolItemAccessor {
	@Accessor("material")
	void setMaterial(ToolMaterial material);
}
