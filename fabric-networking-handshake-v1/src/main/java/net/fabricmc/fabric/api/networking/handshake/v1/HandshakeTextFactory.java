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

package net.fabricmc.fabric.api.networking.handshake.v1;

import net.minecraft.text.Text;

import net.fabricmc.loader.api.Version;

/**
 * A provider of text messages based on whether a client accepts translated messages.
 */
public interface HandshakeTextFactory {
	/**
	 * Returns the default message that is sent when a mod is absent on the client but
	 * required by the server.
	 *
	 * @param modId the mod checked
	 * @return the message
	 */
	Text getAbsentMessage(String modId);

	/**
	 * Returns the default message that is sent when a mod on the client has a version
	 * that is rejected by the server.
	 *
	 * @param modId the mod checked
	 * @param remoteVersion the version on the client
	 * @return the message
	 */
	Text getVersionMismatchMessage(String modId, Version remoteVersion);
}
