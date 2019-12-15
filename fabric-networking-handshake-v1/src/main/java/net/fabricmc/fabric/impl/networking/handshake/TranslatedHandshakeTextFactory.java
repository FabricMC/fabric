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
import net.minecraft.text.TranslatableText;

import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.util.version.VersionParsingException;

public final class TranslatedHandshakeTextFactory implements HandshakeModTextFactory {
	@Override
	public Text getAbsentMessage(String modId) {
		return new TranslatableText("fabric-networking-handshake-v1.missing", modId);
	}

	@Override
	public Text getVersionMismatchMessage(String modId, Version remoteVersion) {
		return new TranslatableText("fabric-networking-handshake-v1.version_rejection", modId, remoteVersion.getFriendlyString());
	}

	@Override
	public Text getErrorsHeader() {
		return new TranslatableText("fabric-networking-handshake-v1.errors");
	}

	@Override
	public Text getBadVersionsMessage(String modId, String badVersion) {
		return new TranslatableText("fabric-networking-handshake-v1.bad_version", modId, badVersion);
	}

	@Override
	public Text getVersionCheckErrorMessage(String modId, Version v, VersionParsingException ex) {
		return new TranslatableText("fabric-networking-handshake-v1.version_check_error", modId, v.getFriendlyString(), ex.getMessage());
	}

	@Override
	public Text getVersionPredicateFailedMessage(String modId, Version v, String st) {
		return new TranslatableText("fabric-networking-handshake-v1.version_predicate_failed", modId, v.getFriendlyString(), st);
	}
}
