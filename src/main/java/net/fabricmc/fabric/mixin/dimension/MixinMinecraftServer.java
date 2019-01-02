/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.mixin.dimension;

import net.fabricmc.fabric.impl.dimension.FabricDimensionComponents;
import net.minecraft.class_3689;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.SecondaryServerWorld;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ServerWorldListener;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.WorldSaveHandler;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.concurrent.ExecutorService;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {
	@Shadow
	@Final
	private ExecutorService field_17200;

	@Shadow
	public abstract class_3689 getProfiler();

	@Shadow
	public abstract ServerWorld getWorld(DimensionType dimensionType_1);

	@Shadow
	@Final
	private Map<DimensionType, ServerWorld> worlds;

	@Inject(method = "createWorlds", at = @At("RETURN"))
	private void createWorlds(WorldSaveHandler saveHandler, PersistentStateManager persistentStateManager, LevelProperties levelProperties, LevelInfo levelInfo, CallbackInfo info) {
		for (DimensionType dimensionType : FabricDimensionComponents.INSTANCE.getModdedDimensionTypes()) {
			SecondaryServerWorld serverWorld = (new SecondaryServerWorld((MinecraftServer) (Object) this, field_17200, saveHandler, dimensionType, getWorld(DimensionType.OVERWORLD), getProfiler())).initializeAsSecondaryWorld();
			worlds.put(dimensionType, serverWorld);
			serverWorld.registerListener(new ServerWorldListener((MinecraftServer) (Object) this, serverWorld));
		}

	}
}
