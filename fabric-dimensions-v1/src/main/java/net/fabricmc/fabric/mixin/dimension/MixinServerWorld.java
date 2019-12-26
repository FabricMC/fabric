package net.fabricmc.fabric.mixin.dimension;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.BooleanSupplier;

@Mixin(ServerWorld.class)
public abstract class MixinServerWorld {
    @Shadow
    @Final
    private MinecraftServer server;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;setTimeOfDay(J)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    private void setTimeInAllWorlds(BooleanSupplier shouldKeepTicking, CallbackInfo info, Profiler profiler, long time) {
    	// `time` is the time of day before it's corrected to be the morning time
        for (World world : server.getWorlds()) {
			DimensionType dimension = world.getDimension().getType();

			// Ignore vanilla dimensions
        	if (dimension == DimensionType.OVERWORLD || dimension == DimensionType.THE_NETHER || dimension == DimensionType.THE_END) {
        		continue;
			}

            world.getLevelProperties().setTimeOfDay(time - time % 24000L);
        }
    }
}
