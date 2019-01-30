package net.fabricmc.fabric.api.event;

import net.fabricmc.fabric.api.event.callbacks.PlayerInteractCallback;
import net.fabricmc.fabric.api.event.listener.ListenerType;
import net.fabricmc.fabric.api.event.listener.ListenerTypeFactory;

public class PlayerInteractionEvents {
	public static final ListenerType<PlayerInteractCallback> ATTACK_BLOCK = ListenerTypeFactory.INSTANCE.create(PlayerInteractCallback.class);
}
