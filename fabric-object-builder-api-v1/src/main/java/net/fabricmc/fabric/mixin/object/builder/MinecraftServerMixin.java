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

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.server.MinecraftServer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricSpawnerRegistry;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
	@SuppressWarnings({"unchecked", "RedundantCast"})
	@Redirect(method = "createWorlds", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList;"))
	<E> ImmutableList<E> fabric_addCustomSpawners(E e1, E e2, E e3, E e4, E e5) {
		List<E> spawners = (List<E>) FabricSpawnerRegistry.getAll().stream().map(Supplier::get).collect(Collectors.toList());
		spawners.add(e1);
		spawners.add(e2);
		spawners.add(e3);
		spawners.add(e4);
		spawners.add(e5);
		return ImmutableList.copyOf(spawners);
	}
}
