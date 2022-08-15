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

package net.fabricmc.fabric.api.object.builder.v1.villager;

import static com.google.common.base.Preconditions.checkState;

import java.util.function.Predicate;

import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

/**
 * Allows for the creation of new {@link VillagerProfession}s.
 *
 * <p>The texture for the villagers are located at <code>assets/IDENTIFIER_NAMESPACE/textures/entity/villager/profession/IDENTIFIER_PATH.png</code>
 *
 * <p>A corresponding <code>IDENTIFIER_PATH.mcmeta</code> file exits in the same directory to define properties such as the {@link net.minecraft.client.render.entity.feature.VillagerResourceMetadata.HatType HatType} this profession would use.
 *
 * <p>Note this does not register any trades to these villagers. To register trades, add a new entry with your profession as the key to {@link TradeOffers#PROFESSION_TO_LEVELED_TRADE}.
 *
 * @deprecated Replaced by access widener for {@link VillagerProfession#VillagerProfession}
 * in Fabric Transitive Access Wideners (v1).
 */
@Deprecated
public final class VillagerProfessionBuilder {
	private final ImmutableSet.Builder<Item> gatherableItemsBuilder = ImmutableSet.builder();
	private final ImmutableSet.Builder<Block> secondaryJobSiteBlockBuilder = ImmutableSet.builder();
	private Identifier identifier;
	private Predicate<RegistryEntry<PointOfInterestType>> pointOfInterestType;
	private Predicate<RegistryEntry<PointOfInterestType>> acquirableJobSite;
	@Nullable
	private SoundEvent workSoundEvent;

	private VillagerProfessionBuilder() {
	}

	/**
	 * Creates a builder instance to allow for creation of a {@link VillagerProfession}.
	 *
	 * @return A new builder.
	 */
	public static VillagerProfessionBuilder create() {
		return new VillagerProfessionBuilder();
	}

	/**
	 * The Identifier used to identify this villager profession.
	 *
	 * @param id The identifier to assign to this profession.
	 * @return this builder
	 */
	public VillagerProfessionBuilder id(Identifier id) {
		this.identifier = id;
		return this;
	}

	/**
	 * The {@link PointOfInterestType} the Villager of this profession will search for when finding a workstation.
	 *
	 * @param key The {@link PointOfInterestType} the Villager will attempt to find.
	 * @return this builder.
	 */
	public VillagerProfessionBuilder workstation(RegistryKey<PointOfInterestType> key) {
		jobSite(entry -> entry.matchesKey(key));
		return workstation(entry -> entry.matchesKey(key));
	}

	/**
	 * The {@link PointOfInterestType} the Villager of this profession will search for when finding a workstation.
	 *
	 * @param predicate The {@link PointOfInterestType} the Villager will attempt to find.
	 * @return this builder.
	 */
	public VillagerProfessionBuilder workstation(Predicate<RegistryEntry<PointOfInterestType>> predicate) {
		this.pointOfInterestType = predicate;
		return this;
	}

	public VillagerProfessionBuilder jobSite(Predicate<RegistryEntry<PointOfInterestType>> predicate) {
		this.acquirableJobSite = predicate;
		return this;
	}

	/**
	 * Items that a Villager may harvest in this profession.
	 *
	 * <p>In Vanilla, this is used by the farmer to define what type of crops the farmer can harvest.
	 *
	 * @param items Items harvestable by this profession.
	 * @return this builder.
	 */
	public VillagerProfessionBuilder harvestableItems(Item... items) {
		this.gatherableItemsBuilder.add(items);
		return this;
	}

	/**
	 * Items that a Villager may harvest in this profession.
	 *
	 * <p>In Vanilla, this is used by the farmer to define what type of crops the farmer can harvest.
	 *
	 * @param items Items harvestable by this profession.
	 * @return this builder.
	 */
	public VillagerProfessionBuilder harvestableItems(Iterable<Item> items) {
		this.gatherableItemsBuilder.addAll(items);
		return this;
	}

	/**
	 * A collection of blocks blocks which may suffice as a secondary job site for a Villager.
	 *
	 * <p>In Vanilla, this is used by the {@link VillagerProfession#FARMER Farmer} to stay near {@link Blocks#FARMLAND Farmland} when at it's job site.
	 *
	 * @param blocks Collection of secondary job site blocks.
	 * @return this builder.
	 */
	public VillagerProfessionBuilder secondaryJobSites(Block... blocks) {
		this.secondaryJobSiteBlockBuilder.add(blocks);
		return this;
	}

	/**
	 * A collection of blocks blocks which may suffice as a secondary job site for a Villager.
	 *
	 * <p>In Vanilla, this is used by the {@link VillagerProfession#FARMER Farmer} to stay near {@link Blocks#FARMLAND Farmland} when at it's job site.
	 *
	 * @param blocks Collection of secondary job site blocks.
	 * @return this builder.
	 */
	public VillagerProfessionBuilder secondaryJobSites(Iterable<Block> blocks) {
		this.secondaryJobSiteBlockBuilder.addAll(blocks);
		return this;
	}

	/**
	 * Provides the sound made when a Villager works.
	 *
	 * @param workSoundEvent The {@link SoundEvent} to be played.
	 * @return this builder.
	 */
	public VillagerProfessionBuilder workSound(@Nullable SoundEvent workSoundEvent) {
		this.workSoundEvent = workSoundEvent;
		return this;
	}

	/**
	 * Creates the {@link VillagerProfession}.
	 *
	 * @return a new {@link VillagerProfession}.
	 * @throws IllegalStateException if the builder is missing an {@link Identifier id} and {@link PointOfInterestType workstation}.
	 */
	public VillagerProfession build() {
		checkState(this.identifier != null, "An Identifier is required to build a new VillagerProfession.");
		checkState(this.pointOfInterestType != null, "A PointOfInterestType is required to build a new VillagerProfession.");
		checkState(this.acquirableJobSite != null, "A PointOfInterestType is required for the acquirableJobSite to build a new VillagerProfession.");

		return new VillagerProfession(this.identifier.toString(), this.pointOfInterestType, this.acquirableJobSite, this.gatherableItemsBuilder.build(), this.secondaryJobSiteBlockBuilder.build(), this.workSoundEvent);
	}
}
