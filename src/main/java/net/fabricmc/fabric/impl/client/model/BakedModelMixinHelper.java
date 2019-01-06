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

package net.fabricmc.fabric.impl.client.model;

import net.fabricmc.fabric.mixin.client.model.MixinBakedModel;
import net.minecraft.util.math.Direction;

public class BakedModelMixinHelper {
    /**
     * This is here for use by {@link MixinBakedModel} which cannot define
     * non-private static fields without Mixin complaining.  The implementation there
     * relies on it to avoid potential creation of new Direction array instances in a hot loop.<p>
     * 
     * This ugliness should be removed as soon as we can target J9 and define
     * private static members in interfaces.
     */
    public static final Direction[] DIRECTIONS = Direction.values();
}
