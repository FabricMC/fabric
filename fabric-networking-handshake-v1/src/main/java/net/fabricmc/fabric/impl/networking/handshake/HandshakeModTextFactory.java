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

package net.fabricmc.fabric.impl.networking.handshake;

import net.minecraft.text.Text;

import net.fabricmc.fabric.api.networking.handshake.v1.HandshakeTextFactory;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.util.version.VersionParsingException;

public interface HandshakeModTextFactory extends HandshakeTextFactory {
	Text getErrorsHeader();

	Text getBadVersionsMessage(String modId, String badVersion);

	Text getVersionCheckErrorMessage(String modId, Version v, VersionParsingException ex);

	Text getVersionPredicateFailedMessage(String modId, Version v, String st);
}
