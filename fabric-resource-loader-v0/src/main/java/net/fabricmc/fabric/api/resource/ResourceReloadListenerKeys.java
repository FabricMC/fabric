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

package net.fabricmc.fabric.api.resource;

import net.minecraft.util.Identifier;

/**
 * This class contains default keys for various Minecraft resource reload listeners.
 *
 * @see IdentifiableResourceReloadListener
 */
public final class ResourceReloadListenerKeys {
	// client
	public static final Identifier SOUNDS = Identifier.method_60656("sounds");
	public static final Identifier FONTS = Identifier.method_60656("fonts");
	public static final Identifier MODELS = Identifier.method_60656("models");
	public static final Identifier LANGUAGES = Identifier.method_60656("languages");
	public static final Identifier TEXTURES = Identifier.method_60656("textures");

	// server
	public static final Identifier TAGS = Identifier.method_60656("tags");
	public static final Identifier RECIPES = Identifier.method_60656("recipes");
	public static final Identifier ADVANCEMENTS = Identifier.method_60656("advancements");
	public static final Identifier FUNCTIONS = Identifier.method_60656("functions");

	private ResourceReloadListenerKeys() { }
}
