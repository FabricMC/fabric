package net.fabricmc.fabric.mixin.idremap;

import net.fabricmc.fabric.impl.dimension.DimensionRemapException;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LevelStorage.class)
public abstract class MixinLevelStorage {
	@ModifyArg(method = "readLevelProperties", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V", remap = false), index = 1)
	private static Object disableRecovery(Object e) {
		if (e instanceof DimensionRemapException) {
			throw (DimensionRemapException) e;
		}
		return e;
	}
}
