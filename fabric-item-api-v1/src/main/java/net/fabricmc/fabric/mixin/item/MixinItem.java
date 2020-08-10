package net.fabricmc.fabric.mixin.item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.minecraft.item.Item;

import net.fabricmc.fabric.api.item.v1.ShieldRegistry;
import net.fabricmc.fabric.impl.item.ItemExtensions;

@Mixin(Item.class)
public class MixinItem implements ItemExtensions {
	@Unique
	private ShieldRegistry.Entry fabric_shieldEntry;

	@Override
	public ShieldRegistry.Entry fabric_getShieldEntry() {
		return fabric_shieldEntry;
	}

	@Override
	public void fabric_setShieldEntry(ShieldRegistry.Entry fabric_shieldEntry) {
		this.fabric_shieldEntry = fabric_shieldEntry;
	}
}
