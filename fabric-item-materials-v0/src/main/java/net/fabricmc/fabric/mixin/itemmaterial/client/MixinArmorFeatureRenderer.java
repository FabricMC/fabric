package net.fabricmc.fabric.mixin.itemmaterial.client;

import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.item.ArmorItem;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ArmorFeatureRenderer.class)
public class MixinArmorFeatureRenderer {
	@Shadow
	@Final
	private static Map<String, Identifier> ARMOR_TEXTURE_CACHE;

	@Inject(at = @At("HEAD"), method = "method_4174", cancellable = true)
	private void method_4174(ArmorItem armorItem, boolean isSecond, String suffix, CallbackInfoReturnable<Identifier> info) {
		Identifier identifier = new Identifier(armorItem.getMaterial().getName());
		String string_2 = identifier.getNamespace() + ":textures/models/armor/" + identifier.getPath() + "_layer_" + (isSecond ? 2 : 1) + (suffix == null ? "" : "_" + suffix) + ".png";
		info.setReturnValue(ARMOR_TEXTURE_CACHE.computeIfAbsent(string_2, Identifier::new));
	}
}
