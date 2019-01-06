/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.api.client.render;

import net.fabricmc.fabric.impl.client.model.VertexFormatRegistryImpl;
import net.minecraft.util.Identifier;

public interface VertexFormatRegistry {
    VertexFormatRegistry INSTANCE = VertexFormatRegistryImpl.INSTANCE;

    /**
     * Makes a vertex format discoverable by other mods.
     * The three standard formats will always be pre-registered.<p>
     * 
     * Returns true of the format was added.  Returns false if 
     * the format was already present.
     * 
     * Format creates are advised not to change formats once released
     * to avoid conflicts. Updated formats should be released with a
     * different identifier.
     */
    boolean register(FabricVertexFormat format);

    /**
     * Retrieve an existing format with the given identifier.
     * Returns null if no matching format is present.
     */
    FabricVertexFormat get(Identifier id);
}
