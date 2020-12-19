package net.fabricmc.fabric.mixin.tags.extension.client.elytra;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.tags.v1.FabricItemTags;

@Mixin(ElytraFeatureRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class ElytraFeatureRendererMixin {
	@Shadow
	@Final private static Identifier SKIN;
	private static Map<Item, Identifier> fabric_tags$TEXTURES;
	private ItemStack fabric_tags$stack;

	@Inject(
			method = "<clinit>",
			at = @At("TAIL")
	)
	private static void staticInit(CallbackInfo ci) {
		fabric_tags$TEXTURES = new Object2ObjectOpenHashMap<>();
	}

	@Redirect(
			method = "render",
			at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"
			)
	)
	private Item render$getItem(ItemStack stack) {
		fabric_tags$stack = stack;
		return FabricItemTags.ELYTRA.contains(stack.getItem()) ? Items.ELYTRA : Items.AIR;
	}

	@ModifyArg(
			method = "render",
			at = @At(
				value = "INVOKE",
				target = "Lnet/minecraft/client/render/RenderLayer;getArmorCutoutNoCull(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;"
			)
	)
	private Identifier render$setSkin(Identifier identifier) {
		if (identifier != SKIN) {
			return identifier;
		}

		Item item = fabric_tags$stack.getItem();
		identifier = fabric_tags$TEXTURES.get(item);

		if (identifier == null) {
			Identifier itemId = Registry.ITEM.getId(item);
			identifier = new Identifier(itemId.getNamespace(), "textures/entity/elytra/" + itemId.getPath() + ".png");
			fabric_tags$TEXTURES.putIfAbsent(item, identifier);
		}

		return identifier;
	}
}
