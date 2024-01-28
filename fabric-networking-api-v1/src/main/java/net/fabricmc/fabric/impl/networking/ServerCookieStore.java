/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.impl.networking;

import java.util.concurrent.CompletableFuture;

import net.minecraft.util.Identifier;

/**
 * Represents something on the server that has the ability to store cookies on the client.
 */
public interface ServerCookieStore {
	/**
	 * Sets the cookie data on the client.
	 *
	 * @param cookieId The id to tag the data with.
	 * @param cookie The data to be set on the client.
	 */
	void setCookie(Identifier cookieId, byte[] cookie);

	/**
	 * Retrieves cookie data from the client.
	 *
	 * @param cookieId The id the data was tagged with.
	 * @return The cookie data or an empty byte[] if there was no cookie found with that id.
	 */
	CompletableFuture<byte[]> getCookie(Identifier cookieId);
}
