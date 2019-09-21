package net.fabricmc.fabric.api.datafixer;

import java.util.Objects;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;

import net.minecraft.datafixers.TypeReferences;
import net.minecraft.datafixers.fixes.BlockNameFix;
import net.minecraft.datafixers.fixes.EntityRenameFix;
import net.minecraft.datafixers.fixes.EntitySimpleTransformFix;
import net.minecraft.datafixers.fixes.FixItemName;

public class SimpleFixes {
    
    /**
     * A basic skeleton DataFix for changing block names.
     * @param builder_1 The builder to add this fix to.
     * @param name The name of the datafix (this has no effect on actual process)
     * @param changes A map of all changed values, where the key should be original and value is new value.
     * @param schema_1 The Schema to apply this fix to.
     */
    public static void addDataFixRenameBlock(DataFixerBuilder builder_1, String name, ImmutableMap<String, String> changes, Schema schema_1) {
        builder_1.addFixer(BlockNameFix.create(schema_1, name, (inputBlockName) -> {
            return changes.getOrDefault(inputBlockName, inputBlockName);
        }));
    }
    
    /**
     * A basic skeleton DataFix for changing item names.
     * @param builder_1 The builder to add this fix to.
     * @param name The name of the datafix (this has no effect on actual process)
     * @param changes A map of all changed values, where the key should be original and value is new value.
     * @param schema_1 The Schema to add this fix to.
     */
    public static void addDataFixRenameItem(DataFixerBuilder builder_1, String name, ImmutableMap<String, String> changes, Schema schema_1) {
        builder_1.addFixer(FixItemName.create(schema_1, name, (inputItemName) -> {
            return changes.getOrDefault(inputItemName, inputItemName);
        }));
    }
    
    /**
     * A basic skeleton DataFix for changing biome names.
     * @param builder_1 The builder to add this fix to.
     * @param name The name of the datafix (this has no effect on actual process)
     * @param changes A map of all changed values, where the key should be original and value is new value.
     * @param schema_1 The Schema to add this fix to.
     */
    public static void addDataFixRenameBiome(DataFixerBuilder builder_1, String name, ImmutableMap<String, String> changes, Schema schema_1) {
        builder_1.addFixer(new DataFix(schema_1, false) {
            @Override
            protected TypeRewriteRule makeRule() {
                Type<Pair<String, String>> type_1 = DSL.named(TypeReferences.BIOME.typeName(), DSL.namespacedString()); // 

                if (!Objects.equals(type_1, this.getInputSchema().getType(TypeReferences.BIOME))) {
                    throw new IllegalStateException("Biome type is not what was expected.");
                } else {
                    return this.fixTypeEverywhere(name, type_1, (dynamicOps_1x) -> { // Fix type_1 using NBTOps basically
                        return (pair_1x) -> {
                            return pair_1x.mapSecond((string_1x) -> {
                                return changes.getOrDefault(string_1x, string_1x);
                            });
                        };
                    });
                }
            }
            
        });
        
    }
    
    /**
     * A basic skeleton DataFix for changing entity names. 
     * <p>Note this does not rename entity spawn eggs and you should use {@link #addDataFixRenameItem(DataFixerBuilder, String, ImmutableMap, Schema)} to rename the spawn egg item.</p>
     * @param builder_1 The builder to add this fix to.
     * @param name The name of the datafix (this has no effect on actual process)
     * @param changes A map of all changed values, where the key should be original and value is new value.
     * @param schema_1 The Schema to add this fix to.
     */
    public static void addDataFixRenameEntity(DataFixerBuilder builder_1, String name, ImmutableMap<String, String> changes, Schema schema_1) {
        builder_1.addFixer(new EntityRenameFix(name, schema_1, false) {

            @Override
            protected String rename(String inputName) {
                return changes.getOrDefault(inputName, inputName);
            }
            
        });
    }
    
    /**
     * A basic skeleton DataFix for transforming an entity.
     * @param builder_1 The builder to add this fix to.
     * @param name The name of the datafix (this has no effect on actual process)
     * @param transformation The transformation to apply to the input entity.
     * @param schema_1 The Schema to add this fix to.
     */
    public static void addDataFixTransformEntity(DataFixerBuilder builder_1, String name, EntityTransformation transformation,  Schema schema_1) {
        builder_1.addFixer(new EntitySimpleTransformFix(name, schema_1, false) {

            @Override
            protected Pair<String, Dynamic<?>> transform(String entityName, Dynamic<?> dynamic) {
                return transformation.transform(entityName, dynamic);
            }
            
        });
    }
    
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
