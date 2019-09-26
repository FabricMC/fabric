package net.fabricmc.fabric.impl.datafixer;

import java.util.Objects;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;

import net.fabricmc.fabric.api.datafixer.SimpleFixes;
import net.fabricmc.fabric.impl.datafixer.fixes.BiomeRenameFix;
import net.fabricmc.fabric.impl.datafixer.fixes.BlockEntityRenameFix;
import net.minecraft.datafixers.fixes.BlockNameFix;
import net.minecraft.datafixers.fixes.EntityRenameFix;
import net.minecraft.datafixers.fixes.EntitySimpleTransformFix;
import net.minecraft.datafixers.fixes.FixItemName;
import net.minecraft.datafixers.schemas.SchemaIdentifierNormalize;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class FabricSimpleFixes implements SimpleFixes {

    public static final SimpleFixes INSTANCE = new FabricSimpleFixes();

    private FabricSimpleFixes() {
    }

    @Override
    public void addBlockRenameFix(DataFixerBuilder builder_1, String name, String oldId, String newId, Schema schema_1) {
        builder_1.addFixer(BlockNameFix.create(schema_1, name, (inputBlockName) -> {
            return Objects.equals(SchemaIdentifierNormalize.normalize(inputBlockName), oldId) ? newId : inputBlockName;
        }));

    }

    @Override
    public void addItemRenameFix(DataFixerBuilder builder_1, String name, String oldId, String newId, Schema schema_1) {
        builder_1.addFixer(FixItemName.create(schema_1, name, (inputItemName) -> {
            return Objects.equals(oldId, inputItemName) ? newId : inputItemName;
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
        builder.addFixer(new BiomeRenameFix(schema, false, name, changes));
    }

    @Override
    public void addEntityTransformFix(DataFixerBuilder builder_1, String name, EntityTransformation transformation, Schema schema_1) {
        builder_1.addFixer(new EntitySimpleTransformFix(name, schema_1, false) {

            @SuppressWarnings("unchecked")
            @Override
            protected Pair<String, Dynamic<?>> transform(String entityName, Dynamic<?> dynamic) {
                return transformation.transform(entityName, (Dynamic<Tag>) dynamic);
            }

        });
    }

    /**
     * Needs testing before release.
     */
    @Override
    public void addBlockEntityRenameFix(DataFixerBuilder builder, String name, String originalBEName, String newBEName, Schema schema) {
        builder.addFixer(new BlockEntityRenameFix(schema, false, originalBEName) {

            @Override
            protected String rename(String inputString) {
                return Objects.equals(inputString, originalBEName) ? newBEName : inputString;
            }
            
        });
    }
}
