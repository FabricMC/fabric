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

package net.fabricmc.fabric.mixin.resource.loader;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

<<<<<<< HEAD:fabric-resource-loader-v0/src/main/java/net/fabricmc/fabric/mixin/resource/loader/FileResourcePackProviderAccessor.java
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;

@Mixin(FileResourcePackProvider.class)
public interface FileResourcePackProviderAccessor {
	@Accessor("field_25345")
	ResourcePackSource getResourcePackSource();
=======
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;

@Mixin(BiomeSource.class)
public interface BiomeSourceAccessor {
	@Accessor
	void setBiomes(List<Biome> biomes);
>>>>>>> 7a4deef8... Ported 1.16.1 biomes-api-v1 to 1.16.2.:fabric-object-builder-api-v1/src/main/java/net/fabricmc/fabric/mixin/object/builder/BiomeSourceAccessor.java
}
