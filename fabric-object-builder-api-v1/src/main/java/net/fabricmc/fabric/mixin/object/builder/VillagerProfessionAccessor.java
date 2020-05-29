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

package net.fabricmc.fabric.mixin.object.builder;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvent;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

@Mixin(VillagerProfession.class)
public interface VillagerProfessionAccessor {
	@Invoker("<init>")
	static VillagerProfession create(String id, PointOfInterestType type, ImmutableSet<Item> gatherableItems, ImmutableSet<Block> secondaryJobSites, /* @Nullable */ SoundEvent soundEvent) {
		throw new AssertionError("Untransformed accessor!");
	}
}
