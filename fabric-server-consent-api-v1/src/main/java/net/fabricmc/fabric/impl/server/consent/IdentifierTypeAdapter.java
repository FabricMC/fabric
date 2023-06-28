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

package net.fabricmc.fabric.impl.server.consent;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import net.minecraft.util.Identifier;

final class IdentifierTypeAdapter extends TypeAdapter<Identifier> {
	@Override
	public void write(JsonWriter writer, Identifier identifier) throws IOException {
		writer.value(identifier.toString());
	}

	@Override
	public Identifier read(JsonReader reader) throws IOException {
		String identifier = reader.nextString();

		if (identifier.indexOf(Identifier.NAMESPACE_SEPARATOR) == -1) {
			return new Identifier("c", identifier);
		}

		return new Identifier(identifier);
	}
}
