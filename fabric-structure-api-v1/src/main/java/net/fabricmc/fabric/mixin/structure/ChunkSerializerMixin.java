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

import java.util.Map;

import it.unimi.dsi.fastutil.longs.LongSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.StructureFeature;

// This is a bug fix, tracking issue: MC-194811
@Mixin(ChunkSerializer.class)
abstract class ChunkSerializerMixin {
	@Unique
	private static final ThreadLocal<Boolean> CHUNK_NEEDS_SAVING = ThreadLocal.withInitial(() -> false);

	/**
	 * Remove objects keyed by `null` in the map.
	 * This data is likely bad since multiple missing structures will cause value mapped by `null` to change at least once.
	 *
	 * <p>If a null value is stored in this map, the chunk will fail to save, so we remove the value stored using null key.
	 *
	 * <p>Note that the chunk may continue to emit errors after being (un)loaded again.
	 * This is because of the way minecraft handles chunk saving.
	 * If the chunk is not modified, the game will keep the currently saved chunk on the disk.
	 * In order to affect this change, we must mark the chunk to be save in order force the game to save the chunk without the errors.
	 */
	@Inject(method = "readStructureReferences", at = @At("TAIL"))
	private static void removeNullKeys(ChunkPos pos, CompoundTag tag, CallbackInfoReturnable<Map<StructureFeature<?>, LongSet>> cir) {
		if (cir.getReturnValue().remove(null) != null) {
			ChunkSerializerMixin.CHUNK_NEEDS_SAVING.set(true);
		}
	}

	@Redirect(method = "deserialize", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;setStructureReferences(Ljava/util/Map;)V"))
	private static void forceChunkSavingIfNullKeysExist(Chunk chunk, Map<StructureFeature<?>, LongSet> structureReferences) {
		// Redirect is much cleaner than local capture. The local capture would be very long
		if (ChunkSerializerMixin.CHUNK_NEEDS_SAVING.get()) {
			ChunkSerializerMixin.CHUNK_NEEDS_SAVING.set(false);
			// Make the chunk save as soon as possible
			chunk.setShouldSave(true);
		}

		// Replicate vanilla logic
		chunk.setStructureReferences(structureReferences);
	}
}
