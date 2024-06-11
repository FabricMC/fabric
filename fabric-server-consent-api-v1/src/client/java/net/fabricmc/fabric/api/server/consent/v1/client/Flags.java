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

package net.fabricmc.fabric.api.server.consent.v1.client;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.util.Identifier;

/**
 * <b>Experimental feature</b>, we reserve the right to remove or change it without further notice.
 */
@ApiStatus.Experimental
public final class Flags {
	public static final String COMMON_NAMESPACE = "c";
	public static final String WILDCARD_FEATURE = "all";

	// A list of common flags
	// ...
	public static final Identifier ARBITRARY_ZOOM = Identifier.of("c", "arbitrary_zoom");
	// ...
}
