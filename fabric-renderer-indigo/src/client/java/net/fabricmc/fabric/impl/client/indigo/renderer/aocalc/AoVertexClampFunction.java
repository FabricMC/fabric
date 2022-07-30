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

package net.fabricmc.fabric.impl.client.indigo.renderer.aocalc;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.impl.client.indigo.Indigo;

@Environment(EnvType.CLIENT)
@FunctionalInterface
interface AoVertexClampFunction {
	float clamp(float x);

	AoVertexClampFunction CLAMP_FUNC = Indigo.FIX_EXTERIOR_VERTEX_LIGHTING ? x -> x < 0f ? 0f : (x > 1f ? 1f : x) : x -> x;
}
