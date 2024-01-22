package net.fabricmc.fabric.impl.networking;

import net.minecraft.util.Identifier;

public interface ServerCookieCallback {
	void fabric_invokeCookieCallback(Identifier cookieId, byte[] cookie);
}
