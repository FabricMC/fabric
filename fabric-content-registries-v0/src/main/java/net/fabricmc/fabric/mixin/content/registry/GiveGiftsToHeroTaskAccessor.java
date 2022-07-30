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

package net.fabricmc.fabric.mixin.content.registry;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.ai.brain.task.GiveGiftsToHeroTask;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerProfession;

@Mixin(GiveGiftsToHeroTask.class)
public interface GiveGiftsToHeroTaskAccessor {
	@Accessor("GIFTS")
	static Map<VillagerProfession, Identifier> fabric_getGifts() {
		throw new AssertionError("Untransformed @Accessor");
	}
}
