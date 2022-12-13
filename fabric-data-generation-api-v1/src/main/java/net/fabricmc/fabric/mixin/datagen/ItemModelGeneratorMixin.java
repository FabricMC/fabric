package net.fabricmc.fabric.mixin.datagen;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import com.google.gson.JsonElement;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.ModelIds;
import net.minecraft.data.client.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.model.FabricItemModelGenerator;

@Mixin(ItemModelGenerator.class)
public class ItemModelGeneratorMixin implements FabricItemModelGenerator {
	@Shadow
	@Final
	public BiConsumer<Identifier, Supplier<JsonElement>> writer;

	@Override
	public void register(Item item, Model model, TextureMap textureMap) {
		model.upload(ModelIds.getItemModelId(item), textureMap, this.writer);
	}

	@Override
	public void register(Item item, String suffix, Model model, TextureMap textureMap) {
		model.upload(ModelIds.getItemSubModelId(item, suffix), textureMap, this.writer);
	}
}
