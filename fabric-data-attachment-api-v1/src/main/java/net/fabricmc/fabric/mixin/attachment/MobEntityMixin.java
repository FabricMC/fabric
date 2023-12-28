package net.fabricmc.fabric.mixin.attachment;

import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;

import net.fabricmc.fabric.impl.attachment.AttachmentTargetImpl;

@Mixin(MobEntity.class)
abstract class MobEntityMixin implements AttachmentTargetImpl {
	@Inject(
			method = "convertTo",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/MobEntity;discard()V")
	)
	private <T extends MobEntity> void copyAttachmentsOnConversion(
			EntityType<T> entityType,
			boolean keepEquipment,
			CallbackInfoReturnable<T> cir,
			@Local MobEntity converted
	) {
		AttachmentTargetImpl.copyOnRespawn(this, (AttachmentTargetImpl) converted, true);
	}
}
