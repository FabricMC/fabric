package net.fabricmc.fabric.mixin.tool.attribute;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.item.MiningToolItem;

@Mixin(MiningToolItem.class)
public interface MiningToolItemAccessor {
	@Accessor("miningSpeed")
	void setMiningSpeed(float miningSpeed);

	@Accessor("miningSpeed")
	float getMiningSpeed();
}
