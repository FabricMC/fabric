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

package net.fabricmc.fabric.impl.object.builder;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

import net.fabricmc.fabric.api.object.builder.v1.villager.VillagerProfessionBuilder;
import net.fabricmc.fabric.mixin.object.builder.VillagerProfessionAccessor;

public class VillagerProfessionBuilderImpl implements VillagerProfessionBuilder {
	private final ImmutableSet.Builder<Item> gatherableItemsBuilder = ImmutableSet.builder();
	private final ImmutableSet.Builder<Block> secondaryJobSiteBlockBuilder = ImmutableSet.builder();

	private Identifier identifier;
	private PointOfInterestType pointOfInterestType;
	private SoundEvent workSoundEvent;

	@Override
	public VillagerProfessionBuilder id(Identifier id) {
		this.identifier = id;
		return this;
	}

	@Override
	public VillagerProfessionBuilder workstation(PointOfInterestType type) {
		this.pointOfInterestType = type;
		return this;
	}

	@Override
	public VillagerProfessionBuilder harvestableItems(Item... items) {
		this.gatherableItemsBuilder.add(items);
		return this;
	}

	@Override
	public VillagerProfessionBuilder harvestableItems(Iterable<Item> items) {
		this.gatherableItemsBuilder.addAll(items);
		return this;
	}

	@Override
	public VillagerProfessionBuilder secondaryJobSites(Block... blocks) {
		this.secondaryJobSiteBlockBuilder.add(blocks);
		return this;
	}

	@Override
	public VillagerProfessionBuilder secondaryJobSites(Iterable<Block> blocks) {
		this.secondaryJobSiteBlockBuilder.addAll(blocks);
		return this;
	}

	@Override
	public VillagerProfessionBuilder workSound(/* @Nullable */ SoundEvent workSoundEvent) {
		this.workSoundEvent = workSoundEvent;
		return this;
	}

	@Override
	public VillagerProfession build() {
		checkState(this.identifier != null, "An Identifier is required to build a new VillagerProfession.");
		checkState(this.pointOfInterestType != null, "A PointOfInterestType is required to build a new VillagerProfession.");
		return VillagerProfessionAccessor.create(this.identifier.toString(), this.pointOfInterestType, this.gatherableItemsBuilder.build(), this.secondaryJobSiteBlockBuilder.build(), this.workSoundEvent);
	}
}
