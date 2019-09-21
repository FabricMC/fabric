package net.fabricmc.fabric.api.datafixer;

import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;

import net.fabricmc.fabric.datafixer.mixin.util.SchemasAccessor;
import net.fabricmc.fabric.impl.datafixer.FabricDataFixerInvoker;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.crash.CrashException;

/**
 * This class is what registers and creates DataFixers for the game.
 * <p><b>Please take extreme caution when using these tools as DataFixers directly interface with the world saves and may corrupt world saves.</b></p>
 * @disclaimer <b>I, i509VCB and the Fabric Team do not take an responsibility for any worlds that are altered, destroyed, corrupted or damaged through the use of the {@link FabricDataFixerUtils} because of improper configuration, mispellings, incorrect syntax, etc.</b>
 * @author i509
 *
 */
public class FabricDataFixerUtils {
    
    private static final FabricDataFixerInvoker IMPL = FabricDataFixerInvoker.INSTANCE;
    
    static boolean LOCKED = false;
    
    public static final BiFunction<Integer,Schema,Schema> IDENTIFIER_NORMALIZE = SchemasAccessor.getIdentNormalize();
    public static final BiFunction<Integer,Schema,Schema> EMPTY = SchemasAccessor.getEmpty();
    
    /**
     * Creates a datafixer for a mod
     * @param modid The modid of the mod
     * @param runtimeDataVersion The current dataVersion of the mod
     * @param builder A consumer in which to build the schema.
     * @return The datafixer.
     */
    public static final DataFixer create(final String modid, final int runtimeDataVersion, final Consumer<DataFixerBuilder> builder) {
        // Validation
        Validate.notNull(modid, "modid cannot be null");
        Validate.finite(runtimeDataVersion, "dataVersion must be finite");
        Validate.inclusiveBetween(0, Integer.MAX_VALUE, runtimeDataVersion, "dataVersion cannot be lower than 0");
        
        DataFixerBuilder builder_1 = new DataFixerBuilder(runtimeDataVersion);
        builder.accept(builder_1);
        return registerFixer(modid, runtimeDataVersion, builder_1);
    }
    
    /**
     * Adds a DataFix to the DataFixer
     * @param builder The datafixerbuilder.
     * @param fix The datafix to add.
     */
    public static void addDataFix(final DataFixerBuilder builder, final DataFix fix) {
        builder.addFixer(fix);
    }

    /**
     * 
     * @param modid The modid of the mod registering this DataFixer
     * @param builder_1 The DataFixer to register
     * @throws CrashException if a DataFixer is registered while the Dedicated/Integrated Server is running.
     */
    private static DataFixer registerFixer(final String modid, final int runtimeDataVersion, DataFixerBuilder builder_1) {

        if(LOCKED) {
            throw new RuntimeException("Tried to register datafixer while server is running");
        }

        // Validation
        Validate.notNull(modid, "modid cannot be null");
        Validate.notNull(builder_1, "DataFixer cannot be null");
        
        DataFixer datafixer = builder_1.build(SystemUtil.getServerWorkerExecutor());
        IMPL.registerFixer(modid, runtimeDataVersion, datafixer);
        
        return datafixer;
    }
    
    @Deprecated
    public static void test() {
        @SuppressWarnings("unused")
        DataFixer modFixer = FabricDataFixerUtils.create("beef", 1, Test::build);
    }
    @Deprecated
    private static class Test {
        private static void build(DataFixerBuilder builder_1) {
            // Make your schema with version to register the fixes under
            Schema schema_1 = builder_1.addSchema(1, IDENTIFIER_NORMALIZE);
            // Add your fix to schema you wish application to occur in.
            SimpleFixes.addDataFixRenameBlock(builder_1, "rename example block", new ImmutableMap.Builder<String, String>().put("test:example1", "test:example").build(), schema_1);
        }
    }
}
