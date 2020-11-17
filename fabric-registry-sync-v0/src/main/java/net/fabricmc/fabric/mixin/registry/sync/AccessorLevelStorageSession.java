package net.fabricmc.fabric.mixin.registry.sync;

import java.nio.file.Path;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.level.storage.LevelStorage;

@Mixin(LevelStorage.Session.class)
public interface AccessorLevelStorageSession {
	@Accessor("directory")
	Path getDirectory();
}
