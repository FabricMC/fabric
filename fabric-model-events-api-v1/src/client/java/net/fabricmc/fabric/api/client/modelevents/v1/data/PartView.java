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

package net.fabricmc.fabric.api.client.modelevents.v1.data;

import java.util.Optional;
import java.util.function.Consumer;

import net.fabricmc.fabric.api.client.modelevents.v1.PartTreePath;
import net.fabricmc.fabric.api.client.modelevents.v1.traversal.Traversable;
import net.fabricmc.fabric.impl.client.modelevents.FabricPartHooks;
import net.minecraft.client.model.ModelPart;

/**
 * Provides access to information about a model part as it is being rendered.
 */
public interface PartView extends Traversable {
    /**
     * Bridge method for converting from a ModelPart back into a PartView.
     *
     * @return Optional associated view if one has been computed.
     */
    static Optional<PartView> of(ModelPart part) {
        return Optional.ofNullable(FabricPartHooks.Container.of(part).getHooks().getView());
    }

    /**
     * The absolute path representing where this part appears within a model's tree
     */
    PartTreePath path();

    /**
     * Provides a direct reference to the ModelPart this view is abstracted over
     */
    ModelPart part();

    /**
     * Data-view of the cubes contained within this part
     */
    DataCollection<CubeData> cubes();

    /**
     * Gets the corresponding view into one of this part's children.
     *
     * @param name Name of the part to locate.
     * @return Optional part view
     */
    Optional<PartView> getChild(String name);

    /**
     * Gets the corresponding view into one of this part's children.
     *
     * @param path Relative path of the child element to find
     * @return Optional part view
     */
    Optional<PartView> getChild(PartTreePath path);

    /**
     * Gets the very top-most element in this part's tree.
     * <p>
     * If this part is an orphan, will return {@code this}
     */
    PartView root();

    /**
     * Iterates through all of the direct descendants of this part.
     *
     * @param partConsumer Consumer for each part view encountered along the tree.
     */
    void forEachChild(Consumer<PartView> childConsumer);
}
