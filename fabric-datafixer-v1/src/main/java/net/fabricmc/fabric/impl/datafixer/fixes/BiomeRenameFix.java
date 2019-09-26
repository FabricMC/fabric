package net.fabricmc.fabric.impl.datafixer.fixes;

import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;

import net.minecraft.datafixers.TypeReferences;

public class BiomeRenameFix extends DataFix {
    private String name;
    private Map<String, String> changes;

    public BiomeRenameFix(Schema outputSchema, boolean changesType, String name, ImmutableMap<String, String> changes) {
        super(outputSchema, changesType);
        this.name = name;
        this.changes = changes;
    }

    @Override
    protected TypeRewriteRule makeRule() {
        Type<Pair<String, String>> type_1 = DSL.named(TypeReferences.BIOME.typeName(), DSL.namespacedString());

        if (!Objects.equals(type_1, this.getInputSchema().getType(TypeReferences.BIOME))) {
            throw new IllegalStateException("Biome type is not what was expected.");
        } else {
            return this.fixTypeEverywhere(name, type_1, (dynamicOps_1x) -> { // Fix type_1 using NBTOps
                return (pair_1x) -> {
                    return pair_1x.mapSecond((string_1x) -> {
                        return changes.getOrDefault(string_1x, string_1x);
                    });
                };
            });
        }
    }
}
