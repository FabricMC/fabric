package net.fabricmc.fabric.test.event.interaction;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.ItemPickupEvent;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;

public class ItemPickupTest implements ModInitializer {
	@Override
	public void onInitialize() {

		ItemPickupEvent.EVENT.register(((player, itemStack) -> {
			if(itemStack.getItem() == Items.DIAMOND)
				return ActionResult.FAIL;
			return ActionResult.SUCCESS;
		}));

	}
}
