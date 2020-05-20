package net.fabricmc.fabric.mixin.dimension;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.impl.dimension.FabricDimensionInternals;
import net.minecraft.class_5285;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.LinkedHashMap;
import java.util.Map;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {

	@Redirect(method = "createWorlds", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_5285;method_28609()Ljava/util/LinkedHashMap;"))
	private LinkedHashMap<RegistryKey<DimensionType>, Pair<DimensionType, ChunkGenerator>> injectDimensions(class_5285 cls) {
		LinkedHashMap<RegistryKey<DimensionType>, Pair<DimensionType, ChunkGenerator>> map = new LinkedHashMap<>(cls.method_28609());

		for (Map.Entry<RegistryKey<DimensionType>, Pair<DimensionType, ChunkGenerator>> entry : FabricDimensionInternals.FABRIC_DIM_MAP.entrySet()) {
			if (map.containsKey(entry.getKey())) {
				throw new RuntimeException("Duplicate dimension id");
			}
			map.put(entry.getKey(), entry.getValue());
		}

		return map;
	}

}
