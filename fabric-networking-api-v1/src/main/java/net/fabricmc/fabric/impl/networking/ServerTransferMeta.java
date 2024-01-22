package net.fabricmc.fabric.impl.networking;

import net.minecraft.util.Identifier;

public interface ServerTransferMeta {

	void fabric_setTransferred();

	void fabric_invokeCookieCallback(Identifier cookieId, byte[] cookie);
}
