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

package net.fabricmc.fabric.api.fluid;

import net.minecraft.util.Identifier;

/**
 * Describes a fluid available in the game. Vanilla fluid types
 * will include water, milk and lava.  All other types are added by mods.
 * 
 * Fluids may or may not have an associated in-world fluid class with fluid states.
 * A fluid without in-world behavior is typically used as a crafting or processing ingredient.
 * 
 * TODO: this is a stub, not fully thought out.
 */
public interface FabricFluidType {
    String translationKey();
    
    Identifier id();
    
    /**
     * RGB color of the fluid. Useful for mods that render fluids using greyscale textures and/or shaders.
     */
    int color();
    
    /** 
     * In degrees Kelvin.  Useful for mods to determine if a fluid is a heat source/sink
     * or potentially dangerous to the player.  Fabric implements no behaviors based on this.
     */
    int temperature();
    
    /**
     * In kilograms per cubic mater.  Use real-world constants when available.
     * Water at sea level, for example, is ~1000.  Informational.  Fabric implements no behaviors based on this.
     */
    float density();
}
