package net.fabricmc.fabric.mixin.dimension;

import net.fabricmc.fabric.api.dimension.EntityTeleporter;
import net.fabricmc.fabric.impl.dimension.FabricDimensionComponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity {

	@Shadow public DimensionType dimension;

	@Shadow public abstract MinecraftServer getServer();

	@Shadow public abstract EntityType<?> getType();

	@Shadow public boolean invalid;

	@Shadow public World world;

	@Shadow private int entityId;

	@Shadow public boolean teleporting;

	@Inject(method = "changeDimension", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V", ordinal = 1, shift = At.Shift.BEFORE), cancellable = true)
	public void changeDimension(DimensionType dimensionType, CallbackInfoReturnable<Entity> infoReturnable) {
		//Right now we only handle this if the entity is travaling to a modded dim,
		EntityTeleporter teleporter = FabricDimensionComponents.INSTANCE.getTeleporter((Entity) (Object)this, dimensionType);
		if(teleporter != null){
			MinecraftServer minecraftServer = this.getServer();
			DimensionType currentDimension = this.dimension;
			ServerWorld oldWorld = minecraftServer.getWorld(currentDimension);
			ServerWorld newWorld = minecraftServer.getWorld(dimensionType);

			oldWorld.updateChunkEntities((Entity) (Object)this);

			Entity newEntity = this.getType().create(newWorld);
			if (newEntity != null) {
				newEntity.method_5878((Entity) (Object)this);


				teleporter.teleport((Entity) (Object)this, oldWorld, newWorld);

				newEntity.setPositionAndAngles(newWorld.getSpawnPos(), newEntity.yaw, newEntity.pitch);

				boolean teleporting = newEntity.teleporting;
				newEntity.teleporting = true;
				newWorld.method_18214(newEntity);
				newEntity.teleporting = teleporting;
				newWorld.updateChunkEntities(newEntity);
			}

			this.invalid = true;
			oldWorld.resetIdleTimeout();
			newWorld.resetIdleTimeout();

			infoReturnable.setReturnValue(newEntity);
		}
	}

}
