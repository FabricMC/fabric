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

package net.fabricmc.fabric.impl.client.render;

import java.util.function.Supplier;

import net.fabricmc.fabric.api.client.render.FabricVertexLighter;

/**
 * Placeholder - not decided where this should go
 * or how to control registration.
 */
public abstract class RenderConfiguration {

    private static Supplier<FabricVertexLighter> lighterFactory = () -> null;

    public static FabricVertexLighter createLighter() {
        return lighterFactory.get();
    }

    private static boolean useConsistentLighting = true;

    /**
     * True if lighter should apply to standard baked models, if present.
     */
    public static boolean useConsistentLighting() {
        return useConsistentLighting;
    }


}
