package net.fabricmc.fabric.test.structure.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.StructureManager;
import net.minecraft.world.ChunkSerializer;

@Mixin(ChunkSerializer.class)
abstract class ChunkSerializerMixin {
	/**
	 * @reason Changes the logging message for the `unknown structure start` to describe which chunk it has occured in for debugging purposes.
	 */
	@ModifyConstant(method = "readStructureStarts", constant = @Constant(stringValue = "Unknown structure start: {}"))
	private static String modifyErrorMessage(String original, StructureManager structureManager, CompoundTag tag, long worldSeed) {
		// Use coordinates in tag to determine the position of the chunk
		final int xPos = tag.getInt("xPos");
		final int zPos = tag.getInt("zPos");

		return String.format("Unknown structure start: {} at chunk [%s, %s]", xPos, zPos);
	}
}
