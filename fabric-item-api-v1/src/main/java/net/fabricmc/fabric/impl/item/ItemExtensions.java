package net.fabricmc.fabric.impl.item;

import net.fabricmc.fabric.api.item.v1.ShieldRegistry;

public interface ItemExtensions {
	ShieldRegistry.Entry fabric_getShieldEntry();

	void fabric_setShieldEntry(ShieldRegistry.Entry fabric_shieldEntry);
}
