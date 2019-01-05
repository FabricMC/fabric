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

package net.fabricmc.fabric.api.client.model;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;

/**
 * Interface for models that use the extended quad/vertex specification
 * (emissive lighting, multi-layer, etc) or dynamic model customization.<p>
 * 
 * <H1>General Notes</H1><p>
 * 
 * Implementations must take care to provide a working {@link #getQuads(BlockState, net.minecraft.util.math.Direction, Random)}
 * method.  The getQuads() method will be used when no rendering plugin is active,
 * and will always be used for block breaking renders.  Mods that do not recognize
 * this interface may also use getQuads() to "wrap" this model, etc.<p>
 * 
 * FabricBakedQuads can be trivially converted to standard BakedQuad instances
 * via {@link FabricBakedQuad#toBakedQuad()}. However, each call results in a new 
 * allocation in the default implementation.<p>
 * 
 * If this model is likely to persist over multiple chunk rebuilds (for block models) or
 * multiple render passes, implementations should ensure BakedQuad instances are cached
 * to avoid reallocating them whenever they are requested.  This can and should be done lazily.
 * 
 * <H1>Item Rendering</H1><p>
 * 
 * Implementations that want to access advanced rendering features for Items
 * should simply return instances of FabricBakedModel from {@link #getItemPropertyOverrides()}.
 * Render plug-ins that support item rendering will then render your item models
 * with whatever enhanced features the plug in supports.<p>
 * 
 * IN ALL CASES the first 28 vertex elements in any item model MUST use an item-compatible format.
 * As with block models, the first layer of your model (if it has more than one) should
 * give a minimally acceptable "default" appearance by itself. This is necessary for compatibility
 * when your model is being rendered without a plug in, or if the current plug in does not 
 * support item rendering.
 * 
 */
public interface FabricBakedModel extends BakedModel, FabricBakedQuadProducer {
	
}
