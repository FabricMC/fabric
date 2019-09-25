package net.fabricmc.fabric.impl.datafixer;

import java.lang.reflect.Field;

import com.google.gson.Gson;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;

import it.unimi.dsi.fastutil.ints.IntSortedSet;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.datafixer.DataFixerUtils;
import net.fabricmc.fabric.api.datafixer.FabricSchemas;
import net.fabricmc.fabric.api.datafixer.SimpleFixes;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.registry.Registry;

public class FabricDataFixerInitalizerCommon implements ModInitializer {
    
    @Override
    public void onInitialize() {
        // TODO Add logic in both Common and Client Initializer to lock DataFixer registration once Client loads or Server starts. Maybe after FabricLoader Entrypoint finishes.
        
        // Ignore test blocks for logic:
        
        //Registry.register(Registry.BLOCK, new Identifier("test:testo"), new Block(FabricBlockSettings.of(Material.CLAY).build())); // For data version 1 // Undefined
        
        Registry.register(Registry.BLOCK, new Identifier("test:test_block"), new Block(FabricBlockSettings.of(Material.CLAY).build())); // For data version 2
        
        // Test DataFixer, will remove later
        DataFixerBuilder builder = new DataFixerBuilder(TEST_DATA_VERSION);
        
        builder.addSchema(0, FabricSchemas.FABRIC_SCHEMA); // This is here to register all the TypeReferences into the DataFixer
        
        Schema v1 = builder.addSchema(1, FabricSchemas.IDENTIFIER_NORMALIZE_SCHEMA);
        SimpleFixes.INSTANCE.addBlockRenameFix(builder, "rename testp to test_block", "test:testo", "test:test_block", v1);
        
        // Remove Reflection Debug hack for release
        DebugHacks.getFixerVersions(builder);
        
        DataFixerUtils.INSTANCE.registerFixer("fabric_test", TEST_DATA_VERSION, builder.build(SystemUtil.getServerWorkerExecutor()));
    }

    public static int TEST_DATA_VERSION = 1;
    
    public static class DebugHacks {
        public static void getFixerVersions(DataFixerBuilder builder) {
            Field field;
            try {
                field = builder.getClass().getDeclaredField("fixerVersions");
            
            
            field.setAccessible(true);
            IntSortedSet fixerVersions = (IntSortedSet) field.get(builder);
            
            
            Gson gson = new Gson();
            
            System.out.println(gson.toJson(fixerVersions));
            } catch (ReflectiveOperationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
}
