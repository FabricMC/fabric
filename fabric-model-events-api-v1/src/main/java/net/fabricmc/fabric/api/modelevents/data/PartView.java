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

package net.fabricmc.fabric.api.modelevents.data;

import net.fabricmc.fabric.api.modelevents.PartTreePath;
import net.minecraft.client.model.ModelPart;

/**
 * Provides access to the information about a model part.
 */
public interface PartView {
    /**
     * The absolute path representing where this part appears within a model's tree
     */
    PartTreePath path();

    /**
     * Provides a direct reference to the ModelPart being rendered
     */
    ModelPart part();

    /**
     * Data-view of the cubes contained within this part
     */
    DataCollection<CubeData> cubes();
}
