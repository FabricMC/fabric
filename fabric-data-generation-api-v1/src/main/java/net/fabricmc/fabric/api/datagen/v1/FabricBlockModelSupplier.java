package net.fabricmc.fabric.api.datagen.v1;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import java.util.HashMap;
import java.util.function.Supplier;

/* Acceptor for the BlockStateModelGenerator's blockStateCollector (datagen) */

public class BlockModelSupplier implements Supplier<JsonElement> {
    protected final JsonObject jsonObject;

    public BlockModelSupplier()
    {
        this.jsonObject = new JsonObject();
    }
  
    // You're expected to manually add a parent type
    // TODO possibly change to be a construction parameter?
    public BlockModelSupplier addParentType(String type)
    {
        this.jsonObject.addProperty("parent", "minecraft:block/" + type);
        return this;
    }

    public BlockModelSupplier addParentType(String modID, String type)
    {
        this.jsonObject.addProperty("parent", modID + ":block/" + type);
        return this;
    }

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
        if (this.jsonObject.get("parent") == null)
        {
            // Default to cube_all if no parent is added!
            this.jsonObject.addProperty("parent", "minecraft:block/cube_all");
        }
        return this.jsonObject;
    }
}
