package net.fabricmc.fabric.mixin.registry.sync;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.ChunkSerializer;

@Mixin(ChunkSerializer.class)
public class MixinChunkSerializer {
	@Redirect(method = "readStructureReferences", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"))
	private static void log(Logger logger, String msg, Object identifier, Object chunkPos) {
		// Drop to debug log level.
		logger.debug(msg, identifier, chunkPos);
	}
}
