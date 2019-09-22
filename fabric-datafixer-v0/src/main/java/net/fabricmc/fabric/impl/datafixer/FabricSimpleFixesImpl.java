package net.fabricmc.fabric.impl.datafixer;

import java.util.Objects;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;

import net.fabricmc.fabric.api.datafixer.SimpleFixes;
import net.minecraft.datafixers.TypeReferences;
import net.minecraft.datafixers.fixes.BlockNameFix;
import net.minecraft.datafixers.fixes.ChoiceFix;
import net.minecraft.datafixers.fixes.EntityRenameFix;
import net.minecraft.datafixers.fixes.EntitySimpleTransformFix;
import net.minecraft.datafixers.fixes.FixItemName;

public class FabricSimpleFixesImpl extends SimpleFixes {

    public static final SimpleFixes INSTANCE = new FabricSimpleFixesImpl();

    private FabricSimpleFixesImpl() {
    }

    @Override
    public void addBlockRenameFix(DataFixerBuilder builder_1, String name, ImmutableMap<String, String> changes, Schema schema_1) {
        builder_1.addFixer(BlockNameFix.create(schema_1, name, (inputBlockName) -> {
            return changes.getOrDefault(inputBlockName, inputBlockName);
        }));

    }

    @Override
    public void addItemRenameFix(DataFixerBuilder builder_1, String name, ImmutableMap<String, String> changes, Schema schema_1) {
        builder_1.addFixer(FixItemName.create(schema_1, name, (inputItemName) -> {
            return changes.getOrDefault(inputItemName, inputItemName);
        }));
    }

    @Override
    public void addEntityRenameFix(DataFixerBuilder builder_1, String name, ImmutableMap<String, String> changes, Schema schema_1) {
        builder_1.addFixer(new EntityRenameFix(name, schema_1, false) {

            @Override
            protected String rename(String inputName) {
                return changes.getOrDefault(inputName, inputName);
            }

        });

    }

    @Override
    public void addBiomeRenameFix(DataFixerBuilder builder, String name, ImmutableMap<String, String> changes, Schema schema) {
        builder.addFixer(new DataFix(schema, false) {
            @Override
            protected TypeRewriteRule makeRule() {
                Type<Pair<String, String>> type_1 = DSL.named(TypeReferences.BIOME.typeName(), DSL.namespacedString()); //

                if (!Objects.equals(type_1, this.getInputSchema().getType(TypeReferences.BIOME))) {
                    throw new IllegalStateException("Biome type is not what was expected.");
                } else {
                    return this.fixTypeEverywhere(name, type_1, (dynamicOps_1x) -> { // Fix type_1 using NBTOps
                                                                                     // basically
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

    @Override
    public void addEntityTransformFix(DataFixerBuilder builder_1, String name, EntityTransformation transformation, Schema schema_1) {
        builder_1.addFixer(new EntitySimpleTransformFix(name, schema_1, false) {

            @Override
            protected Pair<String, Dynamic<?>> transform(String entityName, Dynamic<?> dynamic) {
                return transformation.transform(entityName, dynamic);
            }

        });
    }

    /**
     * Needs testing before release.
     */
    @Override
    public void addBlockEntityRenameFix(DataFixerBuilder builder, String name, String originalBEName, String newBEName, Schema schema) {
        // TODO figure out how it works before implemting

        throw new UnsupportedOperationException("Not implemented yet"); // TODO implement soon
    }
}
