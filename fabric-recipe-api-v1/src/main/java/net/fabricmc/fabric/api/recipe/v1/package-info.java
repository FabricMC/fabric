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

/**
 * The Recipe API, version 1.
 *
 * <p><h3>Quick note about vocabulary in the Recipe API:</h3>
 * <ul>
 *  <li>A static recipe is a recipe which is registered in the API once and is automatically added to
 *  the {@linkplain net.minecraft.recipe.RecipeManager recipe manager} when recipes are loaded.</li>
 *  <li>A dynamic recipe is a recipe which is registered when recipes are loaded.</li>
 * </ul>
 * </p>
 *
 * <p><h3>{@link net.fabricmc.fabric.api.recipe.v1.RecipeManagerHelper RecipeManagerHelper}</h3>
 * The {@link net.fabricmc.fabric.api.recipe.v1.RecipeManagerHelper RecipeManagerHelper} is a helper class focused
 * around the {@link net.minecraft.recipe.RecipeManager}, it allows you to register static and dynamic recipes.
 * </p>
 *
 * <p><h3>{@link net.fabricmc.fabric.api.recipe.v1.event.RecipeLoadingCallback RecipeLoadingCallback}</h3>
 * It is an event which is triggered when recipes are loaded, it allows you to register dynamic recipes.</p>
 */

package net.fabricmc.fabric.api.recipe.v1;
