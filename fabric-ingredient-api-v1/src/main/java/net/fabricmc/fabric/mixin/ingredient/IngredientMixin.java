package net.fabricmc.fabric.mixin.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import net.fabricmc.fabric.api.ingredient.v1.CustomIngredientSerializer;
import net.fabricmc.fabric.api.ingredient.v1.CustomIngredients;
import net.fabricmc.fabric.api.ingredient.v1.FabricIngredient;
import net.fabricmc.fabric.impl.ingredient.CustomIngredientImpl;
import net.fabricmc.fabric.impl.ingredient.builtin.OrIngredient;

// TODO: check for other required mixins (ShapelessRecipe at least)
// TODO: JSON and network serialization and deserialization tests
// TODO: test what happens with vanilla clients
@Mixin(Ingredient.class)
public class IngredientMixin implements FabricIngredient {
	@Shadow
	@Nullable
	private ItemStack[] matchingStacks;

	@Inject(at = @At("HEAD"), method = "cacheMatchingStacks", cancellable = true)
	private void injectCacheMatchingStacks(CallbackInfo ci) {
		if (isCustom() && matchingStacks == null) {
			matchingStacks = getCustomIngredient().getPreviewStacks();
			ci.cancel();
		}
	}

	/**
	 * Inject right when vanilla detected a json object and check for our custom key.
	 */
	@Inject(
			at = @At(
					value = "INVOKE",
					target = "ofEntries(Ljava/util/stream/Stream;)Lnet/minecraft/recipe/Ingredient;",
					ordinal = 0
			),
			method = "fromJson",
			cancellable = true
	)
	public static void injectFromJson(JsonElement json, CallbackInfoReturnable<Ingredient> cir) {
		JsonObject obj = json.getAsJsonObject();

		if (obj.has(CustomIngredientImpl.TYPE_KEY)) {
			Identifier id = new Identifier(JsonHelper.getString(obj, CustomIngredientImpl.TYPE_KEY));
			CustomIngredientSerializer<?> serializer = CustomIngredients.get(id);

			if (serializer != null) {
				cir.setReturnValue(serializer.read(obj).toVanilla());
			} else {
				throw new IllegalArgumentException("Unknown custom ingredient type: " + id);
			}
		}
	}

	/**
	 * Throw exception when someone attempts to use our custom key inside an array ingredient.
	 * The {@link OrIngredient} should be used instead.
	 */
	@Inject(at = @At("HEAD"), method = "entryFromJson")
	public static void injectEntryFromJson(JsonObject obj, CallbackInfoReturnable<?> cir) {
		if (obj.has(CustomIngredientImpl.TYPE_KEY)) {
			throw new IllegalArgumentException("Custom ingredient cannot be used inside an array ingredient");
		}
	}

	/**
	 * @author FabricMC
	 * @reason Support custom ingredient network deserialization.
	 */
	@Overwrite
	public static Ingredient fromPacket(PacketByteBuf buf) {
		int size = buf.readVarInt();

		if (size == CustomIngredientImpl.PACKET_MARKER) {
			Identifier type = buf.readIdentifier();
			CustomIngredientSerializer<?> serializer = CustomIngredients.get(type);

			if (serializer == null) {
				throw new IllegalArgumentException("Cannot deserialize custom ingredient of unkown type " + type);
			}

			return serializer.read(buf).toVanilla();
		} else {
			// Vanilla path - we have to overwrite as we can't read the size a second time.
			ItemStack[] stacks = new ItemStack[size];
			for (int i = 0; i < stacks.length; ++i) stacks[i] = buf.readItemStack();
			return Ingredient.ofStacks(stacks);
		}
	}
}
