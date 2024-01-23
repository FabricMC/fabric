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

package net.fabricmc.fabric.api.networking.v1;

/**
 * Represents something on the server that can be used to transfer a client to a new server.
 */
public interface ServerTransferable {
	/**
	 * Sends the client to a different server. Can also be used on the current server.
	 *
	 * @param host The hostname or ip address of the server to transfer to.
	 * @param port The port of the server to transfer to.
	 */
	void transferToServer(String host, int port);

	/**
	 * @return Whether or not this client joined from a server transfer.
	 */
	boolean wasTransferred();
}
