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

package net.fabricmc.fabric.mixin.structure;

import java.util.Iterator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;

import net.fabricmc.fabric.impl.structure.StructurePlacementContext;

@Mixin(StructureTemplate.class)
public abstract class StructureTemplateMixin {
	@Unique
	private final ThreadLocal<StructurePlacementContext> contextThreadLocal = new ThreadLocal<>();

	@Inject(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/structure/StructureTemplate;spawnEntities(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/BlockMirror;Lnet/minecraft/util/BlockRotation;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockBox;Z)V"))
	private void captureContext(ServerWorldAccess world, BlockPos pos, BlockPos pivot, StructurePlacementData placementData, Random random, int flags, CallbackInfoReturnable<Boolean> cir) {
		contextThreadLocal.set(new StructurePlacementContext(
				world,
				placementData,
				pos,
				pivot
		));
	}

	@Inject(method = "place", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/structure/StructureTemplate;spawnEntities(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/BlockMirror;Lnet/minecraft/util/BlockRotation;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockBox;Z)V"))
	private void clearContext(ServerWorldAccess world, BlockPos pos, BlockPos pivot, StructurePlacementData placementData, Random random, int flags, CallbackInfoReturnable<Boolean> cir) {
		contextThreadLocal.set(null);
	}

	@Redirect(method = "spawnEntities", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;"))
	private Object applyEntityProcessor(Iterator<StructureTemplate.StructureEntityInfo> iterator) {
		final StructurePlacementContext context = contextThreadLocal.get();
		StructureTemplate.StructureEntityInfo entityInfo = iterator.next();

		for (final StructureProcessor processor : context.placementData().getProcessors()) {
			entityInfo = processor.process(
					entityInfo,
					context.worldView(),
					context.pos(),
					context.pivot(),
					context.placementData()
			);

			if (entityInfo == null) {
				// Empty NBT will cause StructureTemplate.getEntity to return an empty Optional, preventing the entity spawn.
				return new StructureTemplate.StructureEntityInfo(Vec3d.ZERO, BlockPos.ORIGIN, new NbtCompound());
			}
		}

		return entityInfo;
	}
}
