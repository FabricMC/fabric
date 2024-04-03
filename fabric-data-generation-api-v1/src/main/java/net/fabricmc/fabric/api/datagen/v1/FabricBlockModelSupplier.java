package net.fabricmc.fabric.api.datagen.v1;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import java.util.HashMap;
import java.util.function.Supplier;

/**
 * Acceptable class for BlockStateModelGenerator's blockStateCollector
 * @see BlockStateModelGenerator
 */

public class BlockModelSupplier implements Supplier<JsonElement> {
    protected final JsonObject jsonObject;

    public BlockModelSupplier(String type)
    {
        this.jsonObject = new JsonObject();
		this.jsonObject.addProperty("parent", "minecraft:block/" + type);
    }

	/**
 	 * Have an acceptable <modID> parameter in case of custom model parents.
	 */
	public BlockModelSupplier(String modID, String type)
    {
        this.jsonObject = new JsonObject();
		this.jsonObject.addProperty("parent", modID + ":block/" + type);
    }

	/**
	 * Add textures.
	 *
	 * @param textureMap The {@link FabricDataGenerator} instance
	 */
    public BlockModelSupplier addTextureData(HashMap<String, Identifier> textureMap)
    {
        JsonObject textureData = new JsonObject();
        for (String key : textureMap.keySet()) {
            Identifier identifier = textureMap.get(key);
            textureData.addProperty(key, identifier.getNamespace() + ":" + identifier.getPath());
        }

        this.jsonObject.add("textures", textureData);
        return this;
    }

    public BlockModelSupplier simpleTextureData(Identifier texture)
    {
        JsonObject textureData = new JsonObject();
        textureData.addProperty("all", texture.getNamespace() + ":" + texture.getPath());

        this.jsonObject.add("textures", textureData);
        return this;
    }

    @Override
    public JsonElement get() {
        return this.jsonObject;
    }
}
