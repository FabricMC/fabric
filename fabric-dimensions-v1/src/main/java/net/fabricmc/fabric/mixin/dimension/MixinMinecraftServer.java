package net.fabricmc.fabric.mixin.dimension;

import java.util.LinkedHashMap;

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.mojang.datafixers.util.Pair;

import net.minecraft.world.SaveProperties;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;

import net.fabricmc.fabric.impl.dimension.FabricDimensionInternals;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
	@Shadow
	@Final
	protected SaveProperties field_24372;

	@Redirect(method = "createWorlds", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/GeneratorOptions;method_28609()Ljava/util/LinkedHashMap;"))
	private LinkedHashMap<RegistryKey<World>, Pair<DimensionType, ChunkGenerator>> injectDimensions(GeneratorOptions generatorOptions) {
		LinkedHashMap<RegistryKey<World>, Pair<DimensionType, ChunkGenerator>> map = new LinkedHashMap<>(generatorOptions.method_28609());

		FabricDimensionInternals.setupWorlds(map, field_24372.method_28057().getSeed());

		return map;
	}
}
