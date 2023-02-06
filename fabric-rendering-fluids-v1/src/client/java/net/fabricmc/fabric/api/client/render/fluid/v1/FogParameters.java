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

package net.fabricmc.fabric.api.client.render.fluid.v1;

import net.minecraft.client.render.FogShape;

/**
 * Provides fog rendering parameters.
 *
 * @param fogStart Distance in blocks, from the camera position, in which the fog starts rendering.
 * @param fogEnd   Distance in blocks, from the camera position, after which the fog is totally opaque.
 * @param fogShape Shape of the fog.
 */
public record FogParameters(float fogStart, float fogEnd, FogShape fogShape) {
}
