package net.fabricmc.fabric.api.event.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public interface ItemPickupEvent {
	Event<ItemPickupEvent> EVENT = EventFactory.createArrayBacked(ItemPickupEvent.class,
			listeners -> (player, itemStack) -> {
				for (ItemPickupEvent event : listeners) {
					ActionResult result = event.interact(player, itemStack);

					if (result != ActionResult.PASS) {
						return result;
					}
				}

				return ActionResult.PASS;
			}
	);

	ActionResult interact(PlayerEntity player, ItemStack itemStack);
}
