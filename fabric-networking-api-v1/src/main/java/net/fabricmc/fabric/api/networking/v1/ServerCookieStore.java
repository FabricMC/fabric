package net.fabricmc.fabric.api.networking.v1;

import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public interface ServerCookieStore {

    void setCookie(Identifier cookieId, byte[] cookie);

    CompletableFuture<byte[]> getCookie(Identifier cookieId);
}
