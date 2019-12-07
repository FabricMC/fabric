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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.server.ServerMetadata;
import net.minecraft.util.JsonHelper;

public final class FabricExtraMeta {
	private List<String> checkedMods;

	public FabricExtraMeta() {
		this.checkedMods = new ArrayList<>();
	}

	public FabricExtraMeta(Set<String> checkedMods) {
		this.checkedMods = new ArrayList<>(checkedMods);
	}

	public static void toJson(ServerMetadata metadata, JsonObject object) {
		FabricExtraMeta meta = ((FabricExtraMetaHolder) metadata).getFabricExtraMeta();
		if (meta == null) return;

		object.add(HandshakeMod.ID, meta.toJson());
	}

	public static void fromJson(JsonObject object, ServerMetadata metadata) {
		JsonObject fabricMetaObj = JsonHelper.getObject(object, HandshakeMod.ID, null);
		if (fabricMetaObj == null) return;

		((FabricExtraMetaHolder) metadata).setFabricExtraMeta(new FabricExtraMeta().fromJson(fabricMetaObj));
	}

	public void setCheckedMods(List<String> checkedMods) {
		this.checkedMods = checkedMods;
	}

	public FabricExtraMeta fromJson(JsonObject object) {
		if (JsonHelper.hasArray(object, "checked_mods")) {
			this.checkedMods.clear();
			JsonArray array = JsonHelper.getArray(object, "checked_mods");

			for (JsonElement each : array) {
				checkedMods.add(each.getAsString());
			}
		}

		return this;
	}

	public JsonObject toJson() {
		JsonObject ret = new JsonObject();

		JsonArray array = new JsonArray();

		for (String s : checkedMods) {
			array.add(s);
		}

		ret.add("checked_mods", array);

		return ret;
	}
}
