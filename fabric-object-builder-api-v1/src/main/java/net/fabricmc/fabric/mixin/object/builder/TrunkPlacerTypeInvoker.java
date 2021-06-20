package net.fabricmc.fabric.mixin.object.builder;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

// Suppress warnings when a {@link} reference a private method since it's invoked by the mixin
@SuppressWarnings("JavadocReference")
@Mixin(TrunkPlacerType.class)
public interface TrunkPlacerTypeInvoker {
	/**
	 * Invokes the {@link TrunkPlacerType#register} method that creates a new {@link TrunkPlacerType}, registers it and returns it
	 */
	@Invoker
	static <T extends TrunkPlacer> TrunkPlacerType<T> invokeRegister(String id, Codec<T> codec) {
		throw new AssertionError();
	}
}
