package net.fabricmc.fabric.mixin.attachment;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;

import net.fabricmc.fabric.impl.attachment.AttachmentTargetImpl;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentTargetInfo;

@Mixin(Chunk.class)
public class ChunkMixin implements AttachmentTargetImpl {
	@Shadow
	@Final
	protected ChunkPos pos;

	@Override
	public AttachmentTargetInfo<?> fabric_getSyncTargetInfo() {
		return new AttachmentTargetInfo.ChunkTarget(this.pos);
	}
}
