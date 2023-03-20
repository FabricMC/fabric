package net.fabricmc.fabric.api.datagen.v1.provider;

import java.util.List;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import net.minecraft.client.texture.atlas.AtlasSource;
import net.minecraft.client.texture.atlas.AtlasSourceManager;
import net.minecraft.data.DataOutput;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

/**
 * Extend this class and implement {@link FabricAtlasSourceProvider#configure}.
 *
 * <p>Register an instance of the class with {@link FabricDataGenerator.Pack#addProvider} in a {@link net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint}.
 */
public abstract class FabricAtlasSourceProvider extends FabricDataProvider<List<AtlasSource>> {
	protected FabricAtlasSourceProvider(FabricDataOutput dataOutput) {
		super(dataOutput, DataOutput.OutputType.RESOURCE_PACK, "atlases");
	}

	@Override
	protected JsonElement convert(Identifier id, List<AtlasSource> value) {
		DataResult<JsonElement> dataResult = AtlasSourceManager.LIST_CODEC.encodeStart(JsonOps.INSTANCE, value);
		return dataResult.result().orElseThrow(() -> new IllegalStateException("Invalid atlas source " + id));
	}

	@Override
	public String getName() {
		return "Atlas Sources";
	}
}
