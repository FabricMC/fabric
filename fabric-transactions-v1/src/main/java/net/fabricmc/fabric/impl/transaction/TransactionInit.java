package net.fabricmc.fabric.impl.transaction;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;

public class TransactionInit implements ModInitializer {
	@Override
	public void onInitialize() {
		ServerStartCallback.EVENT.register(s -> {
			TransactionImpl.setServerThread(s);
		});
	}
}
