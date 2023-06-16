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

package net.fabricmc.fabric.api.server.consent.v1;

import com.google.gson.annotations.SerializedName;

public enum IllegalModResponsePolicy {
	/**
	 * Warn players that are using illegal mods with a message. Along with
	 * this, the configured list of illegal features is sent to the client.
	 * The client can then decide what to do with this information. This
	 * works well if the client is using mods that can locally disable their
	 * features on request.
	 */
	@SerializedName("warn")
	WARN,
	/**
	 * Kick players that are using illegal mods. In the disconnect screen
	 * the client will be presented with a list of the configured illegal
	 * mods.
	 */
	@SerializedName("kick")
	KICK;
}
