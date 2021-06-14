package net.fabricmc.fabric.test.event.interaction;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockApplyCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerPickBlockTests implements ModInitializer {
	@Override
	public void onInitialize() {
		ClientPickBlockApplyCallback.EVENT.register((player, result, stack)->new ItemStack(Items.OBSIDIAN));
	}
}
