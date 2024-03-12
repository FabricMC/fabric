package net.fabricmc.fabric.mixin.datafixer.v1;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;

import net.fabricmc.fabric.api.datafixer.v1.DataFixerEvents;

import net.minecraft.datafixer.schema.Schema1904;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;
import java.util.function.Supplier;

@Mixin(Schema1904.class)
public class Schema1904Mixin {

	@ModifyReturnValue(method = "registerEntities", at = @At("RETURN"))
	private Map<String, Supplier<TypeTemplate>> registerModdedEntities(Map<String, Supplier<TypeTemplate>> original, Schema schema) {
		DataFixerEvents.REGISTER_ENTITIES.invoker().onRegisterEntities(original, schema);
		return original;
	}
}
