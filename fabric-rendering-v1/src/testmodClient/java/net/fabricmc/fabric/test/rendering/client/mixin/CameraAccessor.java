package net.fabricmc.fabric.test.rendering.client.mixin;

import net.minecraft.client.render.Camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)
public interface CameraAccessor {

	@Invoker
	void invokeSetRotation(float yaw, float pitch);
}
