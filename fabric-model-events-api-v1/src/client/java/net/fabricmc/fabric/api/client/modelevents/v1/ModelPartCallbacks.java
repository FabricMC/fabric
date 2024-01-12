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

package net.fabricmc.fabric.api.client.modelevents.v1;

import net.fabricmc.fabric.impl.client.modelevents.ModelPartCallbacksImpl;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

/**
 * Provides events for when individual model parts are rendered.
 * <p>
 * Example usage:
 *
 * <pre>
 * {@code
 * ModelPartCallbacks.get(PartTreePath.of("head")).register(EntityTypes.PLAYER, (player, playerRenderer, partView, matrixStack, vertexConsumerProvider, tickDelta, light) -> {
 *      Vector3f headCenter = partView.cubes().getFirst().getCenter();
 *      matrixStack.push();
 *      matrixStack.translate(headCenter.x, headCenter.y, headCenter.z);
 *      // Draw your model
 *      // Remember to pop your stack
 *      matrixStack.pop();
 * });
 * </pre>
 * }
 */
public interface ModelPartCallbacks {
    /**
     * Gets a callbacks registry to register events upon a certain part is rendered.
     * <p>
     * This is a short-hand method and calling this is the equivalent of calling {@code ModelPartCallbacks.get(ModelPartCallbacks.MatchingStrategy.ENDS_WITH, path)}
     *
     * @param path The path to check for
     * @return ModelPartCallbacks object for registering event listeners.
     */
    static ModelPartCallbacks get(PartTreePath path) {
        return get(MatchingStrategy.ENDS_WITH, path);
    }

    /**
     * Gets a callbacks registry to register events upon a certain part is rendered.
     * <p>
     * The parts that cause events registered through this callback object is determined by the tree-path
     * and the matching strategy.
     *
     * @param matchingStrategy Strategy for deciding whether a model part's path matches the path specified here.
     * @param path The path to check for
     * @return ModelPartCallbacks object for registering event listeners.
     */
    static ModelPartCallbacks get(MatchingStrategy matchingStrategy, PartTreePath path) {
        return ModelPartCallbacksImpl.get(matchingStrategy, path);
    }

    /**
     * Registers an event to be invoked when a model part is rendered.
     */
    void register(ModelPartListener listener);

    /**
     * Registers an event to be invoked for a model part during entity rendering.
     *
     * @param <T> Entity class
     * @param entityType entity type of the entity who's model rendering must trigger this event
     * @param listener Part rendering listener
     */
    <T extends Entity> void register(EntityType<T> entityType, EntityModelPartListener<T> listener);

    /**
     * Registers an event to be invoked for a model part during block entity rendering.
     *
     * @param <T> BlockEntity class
     * @param entityType block entity type of the block entity who's model rendering must trigger this event
     * @param listener Part rendering listener
     */
    <T extends BlockEntity> void register(BlockEntityType<T> entityType, BlockEntityModelPartListener<T> listener);

    /**
     * Represents the strategy used when determining whether a model's path matches with the requested path for an event.
     */
    public enum MatchingStrategy {
        /**
         * The paths are the same.
         */
        EXACT,
        /**
         * The beginning of the model part's path matches, but may have other elements following.
         * <p>
         * Use this to listen for render events for any child nodes of the requested part.
         */
        STARTS_WITH,
        /**
         * The end of the model part's path matches, but may have other elements preceding.
         * <p>
         * Use this to listen for a part who's name (and optionally parents) match the requested path,
         * but may appear nested deep within a model's overall structure.
         * <p>
         * This is the default when registering.
         */
        ENDS_WITH,
        /**
         * The path is matched if the requested path appears anywhere within.
         * Elements may appear before or after the requested path.
         * <p>
         * Use this to listen for any child nodes of the requested part even if it appears nested
         * deep within a model's overall structure.
         */
        CONTAINS
    }
}
