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

import net.fabricmc.fabric.api.client.render.RenderPlugin;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;

/**
 * Interface for models that use extended quad/vertex specification, 
 * dynamic model customization, or rendering features (emissive lighting, 
 * multi-layer textures, etc) supported by the current {@link RenderPlugin}
 * These features will only activate when a rendering plug-in is present.<p>
 * 
 * <H1>General Notes</H1><p>
 * 
 * Implementations must provide a working {@link #getQuads(BlockState, net.minecraft.util.math.Direction, Random)}
 * method.  The getQuads() method will be used when no rendering plugin is active,
 * and will usually be used for block breaking renders and other auxilliary purposes.
 * Mods that do not recognize this interface may also use getQuads() to "wrap" this model, etc.<p>
 * 
 * FabricBakedQuads can be converted to standard BakedQuad instances
 * via {@link FabricBakedQuad#toBakedQuad()}. However, each call results in a new 
 * allocation. If {@link #getQuads(BlockState, net.minecraft.util.math.Direction, Random)}getmodel 
 * is likely to be called multiple times, implementations should ensure BakedQuad instances are cached
 * to avoid reallocating them whenever they are requested.  This can and should be done lazily.<p>
 * 
 * <H1>Item Rendering</H1><p>
 * 
 * Implementations that want to access advanced rendering features for Items
 * should simply return instances of FabricBakedModel from {@link #getItemPropertyOverrides()}.
 * Render plug-ins that support item rendering will then render the item models
 * with whatever enhanced features the plug-in supports.<p>
 * 
 * @see {@link FabricBakedQuad}
 */
public interface FabricBakedModel extends BakedModel, FabricBakedQuadProducer {
	
    /**
     * All implementations of FabricBakedModel are expected to be mutable by default.<p>
     * 
     * "Immutable" in this case means that all <em>public</em> properties will never change
     * and all FabricBakedQuad instance produced by this instance will also be immutable.
     * An immutable instance can therefore be reliably wrapped or aggregated by some other 
     * mod or implementation without copying.<p>
     * 
     * The immutable guarantee extends to side-effects and outputs of {@link #getItemPropertyOverrides()}. 
     * Calling that method will not change any publicly observable attribute of this instance
     * and all models returns will also be immutable. However, the guarantee does <em>not</em> require
     * that the results of {@link #getItemPropertyOverrides()} always be identical.<p>
     * 
     * Consumers that "wrap" or keep a reference to an FabricBakedModel instance should always
     * check {@link #isImmutable()} and if it returns false, obtain an immutable reference via
     * {@link #toImmutable()}.  (Unless some specific functionality in this implementation
     * provides for using or keeping a mutable reference.)<p>
     * 
     * The means for obtaining or editing a mutable instance are left to implementations.
     * Mutable implementations <em>must</em> override this method to return true.
     */
    default boolean isImmutable() {
        // TODO: this should be overridden to true for instances that are known to 
        // be immutable outputs of static model loading.  However, that will require
        // some analysis of subclasses and likely injection scenarios to provide
        // a reliable result.  Not implemented in the current version.
        return false;
    }
}
