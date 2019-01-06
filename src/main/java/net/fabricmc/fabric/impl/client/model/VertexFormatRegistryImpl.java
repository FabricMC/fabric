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

package net.fabricmc.fabric.impl.client.model;

import java.util.HashMap;

import net.fabricmc.fabric.api.client.render.FabricVertexFormat;
import net.fabricmc.fabric.api.client.render.VertexFormatRegistry;
import net.minecraft.util.Identifier;

public class VertexFormatRegistryImpl implements VertexFormatRegistry {
    public static final VertexFormatRegistryImpl INSTANCE = new VertexFormatRegistryImpl();
    
    private final HashMap<Identifier, FabricVertexFormat> map = new HashMap<>();
    
    private VertexFormatRegistryImpl() {
        register(FabricVertexFormatImpl.STANDARD_BLOCK);
        register(FabricVertexFormatImpl.STANDARD_ITEM);
        register(FabricVertexFormatImpl.STANDARD_UNSPECIFIED);
    };
    
    @Override
    public boolean register(FabricVertexFormat format) {
        if(map.containsKey(format.id()))
            return false;
        
        map.put(format.id(), format);
        return true;
    }

    @Override
    public FabricVertexFormat get(Identifier id) {
        return map.get(id);
    }

}
