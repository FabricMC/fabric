package net.fabricmc.fabric.api.datafixer;

import java.util.function.BiFunction;

import com.mojang.datafixers.schemas.Schema;

import net.fabricmc.fabric.impl.datafixer.FabricSchema;
import net.minecraft.datafixers.schemas.SchemaIdentifierNormalize;

public class FabricSchemas {
    /**
     * Fabric Schema Type. This is required for all custom DataFixers or fixing will fail.
     */
    public static final BiFunction<Integer, Schema, Schema> FABRIC_SCHEMA = FabricSchema::new;
    /**
     * Identifier Normalize Schema. 
     */
    public static final BiFunction<Integer,Schema,Schema> IDENTIFIER_NORMALIZE_SCHEMA = SchemaIdentifierNormalize::new;
    
    /**
     * Empty Schema. Nothing special just an empty Schema.
     * @see Schema
     */
    public static final BiFunction<Integer,Schema,Schema> EMPTY_SCHEMA = Schema::new;
}
