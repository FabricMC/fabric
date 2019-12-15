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

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.util.version.VersionParsingException;

public final class HardcodedHandshakeTextFactory implements HandshakeModTextFactory {
	@Override
	public Text getErrorsHeader() {
		return new LiteralText("Mod mismatch has been detected:");
	}

	@Override
	public Text getBadVersionsMessage(String modId, String badVersion) {
		return new LiteralText(String.format("Mod \"%1$s\" has unrecognized version \"%2$s\"", modId, badVersion));
	}

	@Override
	public Text getVersionCheckErrorMessage(String modId, Version v, VersionParsingException ex) {
		return new LiteralText(String.format("Mod \"%1$s\" with version \"%2$s\" has encountered an error in version checking: \"%3$s\"", modId, v.getFriendlyString(), ex.getMessage()));
	}

	@Override
	public Text getVersionPredicateFailedMessage(String modId, Version v, String st) {
		return new LiteralText(String.format("Mod \"%1$s\" requires version \"%3$s\", but detected version \"%2$s\"", modId, v.getFriendlyString(), st));
	}

	@Override
	public Text getAbsentMessage(String modId) {
		return new LiteralText(String.format("Mod \"%1$s\" is missing", modId));
	}

	@Override
	public Text getVersionMismatchMessage(String modId, Version remoteVersion) {
		return new LiteralText(String.format("Mod \"%1$s\" with version \"%2$s\" is rejected", modId, remoteVersion));
	}
}
