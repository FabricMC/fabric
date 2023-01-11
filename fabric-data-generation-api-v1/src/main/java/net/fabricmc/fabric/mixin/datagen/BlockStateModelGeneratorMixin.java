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

package net.fabricmc.fabric.mixin.datagen;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.BlockStateSupplier;
import net.minecraft.data.client.BlockStateVariant;
import net.minecraft.data.client.ModelIds;
import net.minecraft.data.client.VariantSettings;
import net.minecraft.data.client.VariantsBlockStateSupplier;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.model.FabricBlockStateModelGenerator;
import net.fabricmc.fabric.api.datagen.v1.model.builder.ModelBuilder;

@Mixin(BlockStateModelGenerator.class)
public abstract class BlockStateModelGeneratorMixin implements FabricBlockStateModelGenerator {
	@Shadow
	@Final
	public BiConsumer<Identifier, Supplier<JsonElement>> modelCollector;

	@Shadow
	@Final
	public Consumer<BlockStateSupplier> blockStateCollector;

	@Override
	public void registerEmptyModel(Block block) {
		registerEmptyModel(block, ModelIds.getBlockModelId(block));
	}

	@Override
	public void registerEmptyModel(Block block, Identifier id) {
		this.modelCollector.accept(id, JsonObject::new);
		this.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, id));
	}

	@Override
	public void buildWithSingletonState(Block block, ModelBuilder<?> builder) {
		Identifier model = builder.buildModel().upload(block, builder.mapTextures(), this.modelCollector);
		this.blockStateCollector.accept(VariantsBlockStateSupplier.create(block, BlockStateVariant.create().put(VariantSettings.MODEL, model)));
	}
}
