package net.fabricmc.fabric.api.event.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public interface EquipmentTickCallback {
	public static final Event<EquipmentTickCallback> EVENT = EventFactory.createArrayBacked(EquipmentTickCallback.class,
			(listeners) -> (player, inv, slotNum, stack) -> {
				for (EquipmentTickCallback event : listeners) {
					event.tick(player, inv, slotNum, stack);
				}
			}
	);

	void tick(PlayerEntity player, Inventory inv, int slotNumber, ItemStack stack);
}
