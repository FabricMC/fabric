package net.fabricmc.fabric.mixin.renderer.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.render.OverlayVertexConsumer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathConstants;

@Mixin(OverlayVertexConsumer.class)
public class OverlayVertexConsumerMixin {
	@Unique
	private static final Direction[] DIRECTIONS = Direction.values();

	@Redirect(method = "next()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Direction;getFacing(FFF)Lnet/minecraft/util/math/Direction;"))
	private Direction redirectGetFacing(float x, float y, float z) {
		Direction closestDir = Direction.NORTH;
		float maxDot = 1.4E-45F;

		for (Direction direction : DIRECTIONS) {
			float dot = x * direction.getOffsetX() + y * direction.getOffsetY() + z * direction.getOffsetZ();
			if (dot > maxDot + MathConstants.EPSILON) {
				maxDot = dot;
				closestDir = direction;
			}
		}

		return closestDir;
	}
}
