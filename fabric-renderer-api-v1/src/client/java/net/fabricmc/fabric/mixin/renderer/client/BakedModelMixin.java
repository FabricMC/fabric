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

package net.fabricmc.fabric.mixin.renderer.client;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.render.model.BakedModel;

import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;

/**
 * Avoids instanceof checks and enables consistent code path for all baked models.
 */
@Mixin(BakedModel.class)
public interface BakedModelMixin extends FabricBakedModel {
}
