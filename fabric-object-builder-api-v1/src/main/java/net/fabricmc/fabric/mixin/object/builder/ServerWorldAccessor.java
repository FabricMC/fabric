package net.fabricmc.fabric.mixin.object.builder;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.gen.Spawner;

@Mixin(ServerWorld.class)
public interface ServerWorldAccessor {
	@Accessor("spawners")
	List<Spawner> getSpawners();

	@Accessor("spawners")
	void setSpawners(List<Spawner> spawners);
}
