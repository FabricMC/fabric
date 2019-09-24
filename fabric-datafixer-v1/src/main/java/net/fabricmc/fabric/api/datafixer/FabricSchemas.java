package net.fabricmc.fabric.api.datafixer;

import java.util.function.BiFunction;

import com.mojang.datafixers.schemas.Schema;

import net.fabricmc.fabric.impl.datafixer.FabricSchemasImpl;
import net.minecraft.datafixers.schemas.SchemaIdentifierNormalize;

public class FabricSchemas {
    public static final BiFunction<Integer, Schema, Schema> FABRIC_SCHEMA = FabricSchemasImpl.FABRIC_TYPEREF_SCHEMA;
    
    public static final BiFunction<Integer,Schema,Schema> IDENTIFIER_NORMALIZE_SCHEMA = SchemaIdentifierNormalize::new;
    public static final BiFunction<Integer,Schema,Schema> EMPTY_SCHEMA = Schema::new;
}
