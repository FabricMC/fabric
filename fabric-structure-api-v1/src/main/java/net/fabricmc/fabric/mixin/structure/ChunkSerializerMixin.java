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
import net.minecraft.util.Unit;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.StructureFeature;

// This is a bug fix, tracking issue:
@Mixin(ChunkSerializer.class)
abstract class ChunkSerializerMixin {
	@Unique
	private static final ThreadLocal<Unit> CHUNK_NEEDS_SAVING = new ThreadLocal<>();
	/**
	 * Remove objects keyed by `null` in the map.
	 * This data is likely bad since multiple missing structures will cause value mapped by `null` to change at least once.
	 *
	 * If a null value is stored in this map, the chunk will fail to save, so we remove the value stored using null key.
	 *
	 * Note that the chunk may continue to emit errors after being (un)loaded again.
	 * This is because of the way minecraft handles chunk saving.
	 * If the chunk is not modified, the game will keep the currently saved chunk on the disk.
	 * In order to affect this change, we must mark the chunk to be save in order force the game to save the chunk without the errors.
	 */
	@Inject(method = "readStructureReferences", at = @At("TAIL"))
	private static void removeNullKeys(ChunkPos pos, CompoundTag tag, CallbackInfoReturnable<Map<StructureFeature<?>, LongSet>> cir) {
		if (cir.getReturnValue().containsKey(null)) {
			cir.getReturnValue().remove(null);
			ChunkSerializerMixin.CHUNK_NEEDS_SAVING.set(Unit.INSTANCE);
		}
	}

	@Redirect(method = "readStructureStarts", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;setStructureReferences(Ljava/util/Map;)V", shift = At.Shift.AFTER))
	private static void makeChunksDirtyIfMissingStructures(Chunk chunk, Map<StructureFeature<?>, LongSet> structureReferences) {
		if (ChunkSerializerMixin.CHUNK_NEEDS_SAVING.get() != null) {
			ChunkSerializerMixin.CHUNK_NEEDS_SAVING.set(null);
			// Make the chunk save as soon as possible
			chunk.setShouldSave(true);
		}

		// Replicate vanilla logic
		chunk.setStructureReferences(structureReferences);
	}
}
