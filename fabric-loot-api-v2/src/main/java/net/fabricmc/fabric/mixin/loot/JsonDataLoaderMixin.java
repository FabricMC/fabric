package net.fabricmc.fabric.mixin.loot;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.loot.LootDataType;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.loot.LootUtil;

@Mixin(JsonDataLoader.class)
public class JsonDataLoaderMixin {
	@Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourceFinder;toResourceId(Lnet/minecraft/util/Identifier;)Lnet/minecraft/util/Identifier;"))
	private static void fillSourceMap(ResourceManager manager, String dataType, Gson gson, Map<Identifier, JsonElement> results, CallbackInfo ci, @Local Map.Entry<Identifier, Resource> entry) {
		if (!LootDataType.LOOT_TABLES.directory().equals(dataType)) return;

		LootUtil.SOURCES.get().put(entry.getKey(), LootUtil.determineSource(entry.getValue()));
	}
}
