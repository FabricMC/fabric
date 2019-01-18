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

package net.fabricmc.fabric.api.client.model.fabric;

import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.Direction;

/**
 * Used by dynamic models to buffer vertex data at render time.<p>
 * 
 * Note this interface extends {@link FastVertexConsumer}.
 * Models should pre-bake vertex data with {@link FastVertexBuilder} and use 
 * {@link FastVertexConsumer#acceptFastVertexData(ModelMaterial, int, Direction, int[], int, int)}
 * whenever possible.<p>
 * 
 * This interface also extends {@link StandardQuadConsumer}.  Fabric causes vanilla
 * baked models to send their quads via that interface.
 */
public interface DynamicVertexConsumer extends FastVertexConsumer, VertexBuilder, StandardQuadConsumer {
    
    /**
     * Value functions identically to {@link BakedQuad#getColorIndex()} and is
     * used by renderer / model builder in same way.  Value remains in effect
     * for all subsequent quads sent to this consumer until changed. Default value is -1.
     */
    void setQuadColorIndex(int colorIndex);
}
