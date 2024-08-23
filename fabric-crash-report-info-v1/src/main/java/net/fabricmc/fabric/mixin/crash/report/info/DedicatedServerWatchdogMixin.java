package net.fabricmc.fabric.mixin.crash.report.info;

import java.lang.management.ThreadInfo;

import net.fabricmc.fabric.impl.ThreadPrinting;

import net.minecraft.server.dedicated.DedicatedServerWatchdog;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(DedicatedServerWatchdog.class)
public class DedicatedServerWatchdogMixin {

    @ModifyArg(method = "createCrashReport(Ljava/lang/String;J)Lnet/minecraft/util/crash/CrashReport;",
            at = @At(value = "INVOKE",
                    target = "Ljava/lang/StringBuilder;append(Ljava/lang/Object;)Ljava/lang/StringBuilder;",
                    ordinal = 0,
                    remap = false)
    )
    private static Object printEntireThreadDump(Object object) {
        if (object instanceof ThreadInfo threadInfo) {
            return ThreadPrinting.fullThreadInfoToString(threadInfo);
        }
        return object;
    }
}

