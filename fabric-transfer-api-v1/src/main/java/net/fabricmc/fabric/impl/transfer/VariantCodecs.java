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

package net.fabricmc.fabric.impl.transfer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.component.ComponentChanges;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.impl.transfer.fluid.FluidVariantImpl;
import net.fabricmc.fabric.impl.transfer.item.ItemVariantImpl;

public class VariantCodecs {
	public static final Codec<ItemVariant> ITEM_CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Registries.ITEM.getEntryCodec().fieldOf("item").forGetter(ItemVariant::getRegistryEntry),
			ComponentChanges.CODEC.optionalFieldOf("components", ComponentChanges.EMPTY).forGetter(ItemVariant::getComponents)
		).apply(instance, ItemVariantImpl::of)
	);
	public static final PacketCodec<RegistryByteBuf, ItemVariant> ITEM_PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.registryEntry(RegistryKeys.ITEM), ItemVariant::getRegistryEntry,
			ComponentChanges.PACKET_CODEC, ItemVariant::getComponents,
			ItemVariantImpl::of
	);

	public static final Codec<FluidVariant> FLUID_CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Registries.FLUID.getEntryCodec().fieldOf("fluid").forGetter(FluidVariant::getRegistryEntry),
			ComponentChanges.CODEC.optionalFieldOf("components", ComponentChanges.EMPTY).forGetter(FluidVariant::getComponents)
		).apply(instance, FluidVariantImpl::of)
	);
	public static final PacketCodec<RegistryByteBuf, FluidVariant> FLUID_PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.registryEntry(RegistryKeys.FLUID), FluidVariant::getRegistryEntry,
			ComponentChanges.PACKET_CODEC, FluidVariant::getComponents,
			FluidVariantImpl::of
	);
}
