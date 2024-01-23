package net.fabricmc.fabric.api.networking.v1;

import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public interface ServerCookieStore {

	/**
	 * Sets the cookie data on the client
	 *
	 * @param cookieId The id to tag the data with
	 * @param cookie The data to be set on the client
	 */
    void setCookie(Identifier cookieId, byte[] cookie);

	/**
	 * Retrieves cookie data from the client
	 *
	 * @param cookieId The id the data was tagged with
	 * @return The cookie data or an empty byte[] if there was no cookie found with that id
	 */
    CompletableFuture<byte[]> getCookie(Identifier cookieId);
}
