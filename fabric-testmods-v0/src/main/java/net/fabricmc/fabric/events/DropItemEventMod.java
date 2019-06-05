package net.fabricmc.fabric.events;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.DropItemCallback;
import net.minecraft.util.ActionResult;

public class DropItemEventMod implements ModInitializer
{
	@Override
	public void onInitialize()
	{
		DropItemCallback.EVENT.register((playerEntity, world, stack) ->
		{
			System.out.println("item dropped");
			return ActionResult.PASS;
		});
	}
}
