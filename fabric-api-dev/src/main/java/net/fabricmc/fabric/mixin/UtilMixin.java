package net.fabricmc.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.fabricmc.fabric.FabricDev;

import net.minecraft.util.Util;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Util.class)
public class UtilMixin {
	@Dynamic("@ModifyExpressionValue's the FIELD GET of SharedConstants.isDevelopment to add a OR condition for FabricDev.THROW_ON_MISSING_DATA_FIXERS")
	@ModifyExpressionValue(method = "getChoiceTypeInternal", at = @At(value = "FIELD", target = "Lnet/minecraft/SharedConstants;isDevelopment:Z"))
	private static boolean fabric$mevIsDevelopmentForDevModule(boolean original) {
		return original || FabricDev.THROW_ON_MISSING_DATA_FIXERS;
	}

	@Dynamic("@ModifyExpressionValue's the FIELD GET of SharedConstants.isDevelopment to add a OR condition for FabricDev.ENABLE_SUPPLIER_AND_RUNNABLE_DEBUGGING")
	@ModifyExpressionValue(method = {
			"debugRunnable(Ljava/lang/String;Ljava/lang/Runnable;)Ljava/lang/Runnable;",
			"debugSupplier(Ljava/lang/String;Ljava/util/function/Supplier;)Ljava/util/function/Supplier;"
	}, at = @At(value = "FIELD", target = "Lnet/minecraft/SharedConstants;isDevelopment:Z"))
	private static boolean fabric$mevIsDevelopmentForDevModule2(boolean original) {
		return original || FabricDev.ENABLE_SUPPLIER_AND_RUNNABLE_DEBUGGING;
	}

	@Dynamic("@ModifyExpressionValue's the FIELD GET of SharedConstants.isDevelopment to add a OR condition for FabricDev.ENABLE_EXCEPTION_IDE_PAUSING")
	@ModifyExpressionValue(method = {"error", "throwOrPause"}, at = @At(value = "FIELD", target = "Lnet/minecraft/SharedConstants;isDevelopment:Z"))
	private static boolean fabric$mevIsDevelopmentForDevModule3(boolean original) {
		return original || FabricDev.ENABLE_EXCEPTION_IDE_PAUSING;
	}
}
