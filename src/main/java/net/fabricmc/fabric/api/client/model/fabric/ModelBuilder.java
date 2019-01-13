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

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;

/**
 * {@link ModelRenderer#getModelBuilder()} will return an instance of this interface.<p>
 * 
 * The builder is used to create {@link BakedModel} instances that are optimized for the
 * run-time performance of the active renderer.  The renderer controls (and hides) the 
 * implementation of the baked model, and the functionality of the model is defined via 
 * {@link ModelMaterial} and {@link ModelVertexConsumer}.<p>
 * 
 * Baked Models obtained some other way (including standard Minecraft baked models) 
 * will render normally but won't be able to access the enhanced features provided 
 * by the active renderer.<p>
 * 
 * This interface currently does not include an optimal way to implement compound or "wrapped"
 * models that are pre-baked. Future revision may do so.  For now, compound models can be 
 * implemented via a {@link DynamicModelVertexProducer} that pipes the inner model contents to
 * the consumer.  Such implementations should use the array-based methods for vertex transfer
 * as much as possible.<p>
 * 
 * This interface is currently targeted at block models. It can be used to generate
 * baked item models that are then used to build a {@link ModelItemPropertyOverrideList}.
 * But it does not support truly dynamic item rendering beyond what that class provides.
 * Future revisions may provide more dynamic item rendering features.
 */
public interface ModelBuilder extends ModelVertexConsumer {
    /**
     * Creates a {@link BakedModel} instance containing all of the quads input to this instance
     * prior to the call.  Implicitly calls {@link #end()} for the current quad if needed.
     * At the end of the call, this builder instance is cleared and ready for input of another model.<p>
     * 
     * @param isItem  If true, this model is intended for item rendering. This means the model
     * will return item-format vertex data from {@link BakedModel#getQuads()}.  If false, the model is
     * intended for block rendering and should return block-format vertex data.<p>
     * 
     * @param sprite  Texture to be used for block particles. Necessary to implement {@link BakedModel}.<p>
     * 
     * @param transformation    Defines spatial transformations for rendering this model in different
     * contexts. Important for item models.  Pass {@link ModelTransformation#ORIGIN} when not applicable.<p>
     * 
     * @param itemPropertyOverrides List of model variations to be used for item rendering. 
     * Important for item models.  Pass {@link ModelItemPropertyOverrideList#ORIGIN} when not applicable.<p>
     * 
     * @return  Baked model that supports enhanced features.  See header for additional info.
     */
    BakedModel buildStatic(boolean isItem, Sprite sprite, ModelTransformation transformation, ModelItemPropertyOverrideList itemPropertyOverrides);
    
    /**
     * Identical to {@link #buildStatic(boolean, Sprite, ModelTransformation, ModelItemPropertyOverrideList)} except
     * the resulting model outputs additional quads during chunk rebuild via the provided
     * {@link DynamicModelVertexProducer} instance.<p>
     * 
     * Dynamic models are not required to have any static quads. It is valid to call this method without first
     * adding any quads to the model builder. However, for performance it is best to input quads that are fully 
     * determined by block state before the model is built, and reserve {@link DynamicModelVertexProducer} 
     * for quads that depend on world state.
     */
    BakedModel buildDynamic(DynamicModelVertexProducer producer, Sprite sprite, ModelTransformation transformation, ModelItemPropertyOverrideList itemPropertyOverrides);
}
