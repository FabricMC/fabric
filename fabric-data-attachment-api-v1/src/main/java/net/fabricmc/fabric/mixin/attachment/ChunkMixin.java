package net.fabricmc.fabric.mixin.attachment;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.impl.attachment.AttachmentEntrypoint;
import net.fabricmc.fabric.impl.attachment.AttachmentTargetImpl;
import net.fabricmc.fabric.impl.attachment.sync.AttachmentTargetInfo;

@Mixin(Chunk.class)
public abstract class ChunkMixin implements AttachmentTargetImpl {
	@Shadow
	@Final
	protected ChunkPos pos;

	@Shadow
	public abstract void setNeedsSaving(boolean needsSaving);

	@Shadow
	public abstract ChunkStatus getStatus();

	@Override
	public AttachmentTargetInfo<?> fabric_getSyncTargetInfo() {
		return new AttachmentTargetInfo.ChunkTarget(this.pos);
	}

	@Override
	public void fabric_markChanged(AttachmentType<?> type) {
		this.setNeedsSaving(true);

		if (type.isPersistent() && this.getStatus().equals(ChunkStatus.EMPTY)) {
			AttachmentEntrypoint.LOGGER.warn(
					"Attaching persistent attachment {} to chunk with chunk status EMPTY. Attachment might be discarded.",
					type.identifier()
			);
		}
	}
}
