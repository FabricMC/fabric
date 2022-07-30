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

package net.fabricmc.fabric.api.client.texture;

import java.util.Set;

import net.minecraft.util.Identifier;

/**
 * Implement this interface on a Sprite to declare additional dependencies
 * that should be processed prior to this sprite.
 *
 * <p>Best used in conjunction with {@link net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback}.
 */
public interface DependentSprite {
	/**
	 * @return A set of all sprites that should be loaded before this sprite.
	 */
	Set<Identifier> getDependencies();
}
