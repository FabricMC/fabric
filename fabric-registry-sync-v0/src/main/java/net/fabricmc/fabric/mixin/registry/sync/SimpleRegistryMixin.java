package net.fabricmc.fabric.mixin.registry.sync;

import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Lifecycle;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.IdentityHashMap;

// Use larger expected sizes for other mods
@Mixin(SimpleRegistry.class)
public class SimpleRegistryMixin<T> {

	@Redirect(
			method = "<init>",
			at = @At(
					value = "INVOKE",
					target = "Lcom/google/common/collect/HashBiMap;create()Lcom/google/common/collect/HashBiMap;",
					opcode = 0
			)
	)
	private HashBiMap<Identifier, T> increaseIdToEntryMapSize() {
		return HashBiMap.create(2048);
	}

	@Redirect(
			method = "<init>",
			at = @At(
					value = "INVOKE",
					target = "Lcom/google/common/collect/HashBiMap;create()Lcom/google/common/collect/HashBiMap;",
					opcode = 1
			)
	)
	private HashBiMap<RegistryKey<T>, T> increaseKeyToEntryMapSize() {
		return HashBiMap.create(2048);
	}

	@Redirect(
			method = "<init>",
			at = @At(
					value = "INVOKE",
					target = "Lcom/google/common/collect/Maps;newIdentityHashMap()Ljava/util/IdentityHashMap;",
					opcode = 0
			)
	)
	private IdentityHashMap<T, Lifecycle> increaseEntryToLifecycleMapSize() {
		return new IdentityHashMap<>(2048);
	}
}
