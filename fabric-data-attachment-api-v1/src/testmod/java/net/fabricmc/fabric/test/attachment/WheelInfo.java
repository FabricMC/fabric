package net.fabricmc.fabric.test.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

import net.minecraft.util.dynamic.Codecs;

public record WheelInfo(float wheelDiameter, float tireDiameter, float tireThickness) {
	public static final Codec<WheelInfo> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codecs.POSITIVE_FLOAT.fieldOf("wheelDiameter").forGetter(WheelInfo::wheelDiameter),
			Codecs.POSITIVE_FLOAT.fieldOf("tireDiameter").forGetter(WheelInfo::tireDiameter),
			Codecs.POSITIVE_FLOAT.fieldOf("tireThickness").forGetter(WheelInfo::tireThickness)
	).apply(instance, WheelInfo::new));

	public static final AttachmentType<WheelInfo> ATTACHMENT = AttachmentRegistry.create("wheel_info",
			attachment -> attachment.initializer(() -> new WheelInfo(100, 5432, 37))
					.persistent(WheelInfo.CODEC)
	);
}
