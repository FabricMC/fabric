package net.fabricmc.fabric.api.datafixer;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;

import net.fabricmc.fabric.impl.datafixer.FabricSimpleFixes;

/**
 * This class contains several common datafixes modders would use.
 */
public interface SimpleFixes {
    
    public static final SimpleFixes INSTANCE = FabricSimpleFixes.INSTANCE;

    /**
     * A basic DataFix for changing block names.
     * @param builder The builder to add this fix to.
     * @param name The name of the datafix (this has no effect on actual process)
     * @param changes A map of all changed values, where the key should be original and value is new value.
     * @param schema The Schema to apply this fix to.
     */
    public abstract void addBlockRenameFix(DataFixerBuilder builder_1, String name, String oldId, String newId, Schema schema_1);

    /**
     * A basic DataFix for changing item names.
     * @param builder The builder to add this fix to.
     * @param name The name of the datafix (this has no effect on actual process)
     * @param changes A map of all changed values, where the key should be original and value is new value.
     * @param schema The Schema to add this fix to.
     */
    public abstract void addItemRenameFix(DataFixerBuilder builder_1, String name, String oldId, String newId, Schema schema_1);

    /**
     * A basic DataFix for changing entity names. 
     * <p>Note this does not rename entity spawn eggs and you should use {@link #addItemRenameFix(DataFixerBuilder, String, ImmutableMap, Schema)} to rename the spawn egg item.</p>
     * @param builder The builder to add this fix to.
     * @param name The name of the datafix (this has no effect on actual process)
     * @param changes A map of all changed values, where the key should be original and value is new value.
     * @param schema The Schema to add this fix to.
     */
    public abstract void addEntityRenameFix(DataFixerBuilder builder, String name, ImmutableMap<String, String> changes, Schema schema);

    /**
     * A basic DataFix for transforming an entity.
     * @param builder The builder to add this fix to.
     * @param name The name of the DataFix (this has no effect on actual process)
     * @param transformation The transformation to apply to the input entity.
     * @param schema The Schema to add this fix to.
     */
    public abstract void addEntityTransformFix(DataFixerBuilder builder, String name, EntityTransformation transformation, Schema schema);
    
    /**
     * A basic DataFix for changing biome names.
     * @param builder The builder to add this fix to.
     * @param name The name of the datafix (this has no effect on actual process)
     * @param changes A map of all changed values, where the key should be original and value is new value.
     * @param schema The Schema to add this fix to.
     */
    public abstract void addBiomeRenameFix(DataFixerBuilder builder, String name, ImmutableMap<String, String> changes, Schema schema);
    
    /**
     * A basic DataFix for changing blockentity names
     * @param builder The builder to add this fix to.
     * @param name The name of the datafix (this has no effect on actual process)
     * @param originalBEName the original name of the BlockEntity
     * @param newBEName the new desired name of the BlockEntity being renamed.
     * @param schema The Schema to add this fix to.
     */
    public abstract void addBlockEntityRenameFix(DataFixerBuilder builder, String name, String originalBEName, String newBEName, Schema schema);
    
    /**
     * Represents an entity transformation function for a datafix.
     */
    @FunctionalInterface
    public interface EntityTransformation {
        
        /**
         * Transforms an entity.
         * @param inputEntityName The input entity's name.
         * @param dynamic The Dynamic object representing the entity.
         * @return A Pair which contains the entity's new name and the dynamic representing the entity. 
         */
        public Pair<String, Dynamic<?>> transform(String inputEntityName, Dynamic<?> dynamic);
    }

}
