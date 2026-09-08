package net.fabricmc.fabric.mixin.datagen.advancement;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.minecraft.core.Holder;
import net.minecraft.data.advancements.packs.VanillaAdventureAdvancements;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.stream.Stream;

@Mixin(VanillaAdventureAdvancements.class)
public class VanillaAdventureAdvancementsMixin {
	@ModifyExpressionValue(method = "validateMobsToKill", at = @At(value = "INVOKE", target = "Lnet/minecraft/data/worldgen/BootstrapContext;listContextElements(Lnet/minecraft/resources/ResourceKey;)Ljava/util/stream/Stream;"))
	private Stream<Holder.Reference<EntityType<?>>> onlyCheckVanillaEntities(Stream<Holder.Reference<EntityType<?>>> entities) {
		return entities.filter(entity -> entity.key().identifier().getNamespace().equals(Identifier.DEFAULT_NAMESPACE));
	}
}
