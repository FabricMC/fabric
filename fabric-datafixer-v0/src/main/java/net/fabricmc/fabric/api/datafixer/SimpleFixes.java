package net.fabricmc.fabric.api.datafixer;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;

import net.fabricmc.fabric.impl.datafixer.FabricSimpleFixesImpl;
import net.minecraft.datafixers.fixes.EntitySimpleTransformFix;

public abstract class SimpleFixes {
    
    private static SimpleFixes INSTANCE = FabricSimpleFixesImpl.INSTANCE;
    
    /**
     * A basic skeleton DataFix for changing block names.
     * @param builder_1 The builder to add this fix to.
     * @param name The name of the datafix (this has no effect on actual process)
     * @param changes A map of all changed values, where the key should be original and value is new value.
     * @param schema_1 The Schema to apply this fix to.
     */
    public static void addBlockRenameFix(DataFixerBuilder builder_1, String name, ImmutableMap<String, String> changes, Schema schema_1) {
        INSTANCE._addBlockRenameFix(builder_1, name, changes, schema_1);
    }
    
    /**
     * A basic skeleton DataFix for changing item names.
     * @param builder_1 The builder to add this fix to.
     * @param name The name of the datafix (this has no effect on actual process)
     * @param changes A map of all changed values, where the key should be original and value is new value.
     * @param schema_1 The Schema to add this fix to.
     */
    public static void addItemRenameFix(DataFixerBuilder builder_1, String name, ImmutableMap<String, String> changes, Schema schema_1) {
        INSTANCE._addItemRenameFix(builder_1, name, changes, schema_1);
    }
    
    /**
     * A basic skeleton DataFix for changing biome names.
     * @param builder_1 The builder to add this fix to.
     * @param name The name of the datafix (this has no effect on actual process)
     * @param changes A map of all changed values, where the key should be original and value is new value.
     * @param schema_1 The Schema to add this fix to.
     */
    public static void addBiomeRenameFix(DataFixerBuilder builder_1, String name, ImmutableMap<String, String> changes, Schema schema_1) {
        INSTANCE._addBiomeRenameFix(builder_1, name, changes, schema_1);
    }
    
    /**
     * A basic skeleton DataFix for changing entity names. 
     * <p>Note this does not rename entity spawn eggs and you should use {@link #addItemRenameFix(DataFixerBuilder, String, ImmutableMap, Schema)} to rename the spawn egg item.</p>
     * @param builder_1 The builder to add this fix to.
     * @param name The name of the datafix (this has no effect on actual process)
     * @param changes A map of all changed values, where the key should be original and value is new value.
     * @param schema_1 The Schema to add this fix to.
     */
    public static void addEntityRenameFix(DataFixerBuilder builder_1, String name, ImmutableMap<String, String> changes, Schema schema_1) {
        INSTANCE._addEntityRenameFix(builder_1, name, changes, schema_1);
    }
    
    /**
     * A basic skeleton DataFix for transforming an entity.
     * @param builder_1 The builder to add this fix to.
     * @param name The name of the datafix (this has no effect on actual process)
     * @param transformation The transformation to apply to the input entity.
     * @param schema_1 The Schema to add this fix to.
     */
    public static void addDataFixTransformEntity(DataFixerBuilder builder_1, String name, EntityTransformation transformation, Schema schema_1) {
        INSTANCE._addTransformEntityFix(builder_1, name, transformation, schema_1);
    }
    
    protected abstract void _addTransformEntityFix(DataFixerBuilder builder_1, String name, EntityTransformation transformation, Schema schema_1);

    protected abstract void _addBiomeRenameFix(DataFixerBuilder builder_1, String name, ImmutableMap<String, String> changes, Schema schema_1);

    protected abstract void _addBlockRenameFix(DataFixerBuilder builder_1, String name, ImmutableMap<String, String> changes, Schema schema_1);

    protected abstract void _addEntityRenameFix(DataFixerBuilder builder_1, String name, ImmutableMap<String, String> changes, Schema schema_1);

    protected abstract void _addItemRenameFix(DataFixerBuilder builder_1, String name, ImmutableMap<String, String> changes, Schema schema_1);

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
