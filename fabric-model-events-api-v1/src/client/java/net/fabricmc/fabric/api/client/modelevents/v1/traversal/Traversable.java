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

package net.fabricmc.fabric.api.client.modelevents.v1.traversal;

import net.minecraft.client.util.math.MatrixStack;

/**
 * Represents an object that can be traversed.
 *
 * @see PartView
 * @see CubeData
 * @see FaceData
 */
public interface Traversable {
    void traverse(MatrixStack matrices, ModelVisitor visitor);
}
