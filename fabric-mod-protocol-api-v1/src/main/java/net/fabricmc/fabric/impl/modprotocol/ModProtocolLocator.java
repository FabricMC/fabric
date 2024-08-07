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

package net.fabricmc.fabric.impl.modprotocol;

import java.util.function.BiConsumer;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import net.minecraft.util.Identifier;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;

public class ModProtocolLocator {
	public static void provide(BiConsumer<ModContainer, ModProtocolImpl> consumer) {
		for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
			create(mod, consumer);
		}
	}

	private static void create(ModContainer container, BiConsumer<ModContainer, ModProtocolImpl> consumer) {
		ModMetadata meta = container.getMetadata();
		CustomValue definition = meta.getCustomValue("fabric:mod_protocol");

		if (definition == null) {
			return;
		}

		if (definition.getType() == CustomValue.CvType.ARRAY) {
			for (CustomValue entry : definition.getAsArray()) {
				consumer.accept(container, decodeFullDefinition(entry, meta, true));
			}
		} else if (definition.getType() == CustomValue.CvType.NUMBER) {
			consumer.accept(container, new ModProtocolImpl(Identifier.of("mod", meta.getId()), meta.getName(), meta.getVersion().getFriendlyString(), IntList.of(definition.getAsNumber().intValue()), true, true));
		} else {
			consumer.accept(container, decodeFullDefinition(definition, meta, false));
		}
	}

	public static ModProtocolImpl decodeFullDefinition(CustomValue entry, ModMetadata meta, boolean requireFullData) {
		if (entry.getType() != CustomValue.CvType.OBJECT) {
			throw new RuntimeException("Mod Protocol entry provided by '" + meta.getId() + "' is not valid!");
		}

		CustomValue.CvObject object = entry.getAsObject();
		Identifier id;
		String name;
		String version;
		boolean requiredClient;
		boolean requiredServer;
		IntList protocols = new IntArrayList();

		CustomValue idField = object.get("id");
		CustomValue nameField = object.get("name");
		CustomValue versionField = object.get("version");
		CustomValue protocolField = object.get("protocol");
		CustomValue requiredClientField = object.get("require_client");
		CustomValue requiredServerField = object.get("require_server");

		if (!requireFullData && idField == null) {
			id = Identifier.of("mod", meta.getId());
		} else if (idField != null && idField.getType() == CustomValue.CvType.STRING) {
			id = Identifier.of(idField.getAsString());
		} else {
			throw new RuntimeException("Mod Protocol entry provided by '" + meta.getId() + "' is not valid!");
		}

		if (protocolField != null && protocolField.getType() == CustomValue.CvType.NUMBER) {
			protocols.add(protocolField.getAsNumber().intValue());
		} else if (protocolField != null && protocolField.getType() == CustomValue.CvType.ARRAY) {
			for (CustomValue value : protocolField.getAsArray()) {
				if (value.getType() == CustomValue.CvType.NUMBER) {
					protocols.add(value.getAsNumber().intValue());
				} else {
					throw new RuntimeException("Mod Protocol entry provided by '" + meta.getId() + "' is not valid!");
				}
			}
		} else {
			throw new RuntimeException("Mod Protocol entry provided by '" + meta.getId() + "' is not valid!");
		}

		if (!requireFullData && nameField == null) {
			name = meta.getName();
		} else if (nameField != null && nameField.getType() == CustomValue.CvType.STRING) {
			name = nameField.getAsString();
		} else {
			throw new RuntimeException("Mod Protocol entry provided by '" + meta.getId() + "' is not valid!");
		}

		if (!requireFullData && versionField == null) {
			version = meta.getName();
		} else if (versionField != null && versionField.getType() == CustomValue.CvType.STRING) {
			version = versionField.getAsString();
		} else {
			throw new RuntimeException("Mod Protocol entry provided by '" + meta.getId() + "' is not valid!");
		}

		if (requiredClientField == null) {
			requiredClient = true;
		} else if (requiredClientField.getType() == CustomValue.CvType.BOOLEAN) {
			requiredClient = requiredClientField.getAsBoolean();
		} else {
			throw new RuntimeException("Mod Protocol entry provided by '" + meta.getId() + "' is not valid!");
		}

		if (requiredServerField == null) {
			requiredServer = true;
		} else if (requiredServerField.getType() == CustomValue.CvType.BOOLEAN) {
			requiredServer = requiredServerField.getAsBoolean();
		} else {
			throw new RuntimeException("Mod Protocol entry provided by '" + meta.getId() + "' is not valid!");
		}

		return new ModProtocolImpl(id, name, version, IntList.of(protocols.toIntArray()), requiredClient, requiredServer);
	}
}
