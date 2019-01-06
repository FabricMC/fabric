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

import net.fabricmc.fabric.api.client.render.FabricVertexFormat;
import net.minecraft.util.Identifier;

public class FabricVertexFormatImpl implements FabricVertexFormat {
    public static final FabricVertexFormatImpl STANDARD_ITEM = new FabricVertexFormatImpl(
            new Identifier("minecraft", "item"), true, false);
    
    public static final FabricVertexFormatImpl STANDARD_BLOCK = new FabricVertexFormatImpl(
            new Identifier("minecraft", "block"), false, true);
    
    public static final FabricVertexFormatImpl STANDARD_UNSPECIFIED = new FabricVertexFormatImpl(
            new Identifier("minecraft", "unspecified"), true, true);
    
    private final Identifier id;
    private final boolean isItemModelCompatible;
    private final boolean isBlockModelCompatible;

    FabricVertexFormatImpl(Identifier id, boolean isItem, boolean isBlock) {
        this.id = id;
        this.isItemModelCompatible = isItem;
        this.isBlockModelCompatible = isBlock;
    }
    
    @Override
    public Identifier id() {
        return id;
    }

    @Override
    public int integerQuadStride() {
        return 28;
    }

    @Override
    public boolean isItemModelCompatible() {
        return isItemModelCompatible;
    }

    @Override
    public boolean isBlockModelCompatible() {
        return isBlockModelCompatible;
    }

    @Override
    public int textureDepth() {
        return 1;
    }
}
