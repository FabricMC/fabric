package net.fabricmc.fabric.api.datafixer;

import java.util.function.BiFunction;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;

import net.fabricmc.fabric.impl.datafixer.FabricDataFixerImpl;
import net.minecraft.datafixers.schemas.SchemaIdentifierNormalize;
import net.minecraft.util.crash.CrashException;

/**
 * This registers and creates DataFixers for the game.
 * <p><b>Please take extreme caution when using these tools as DataFixers directly interface with the world saves and may corrupt world saves.</b></p>
 * @disclaimer <b>I, i509VCB and the Fabric Team do not take an responsibility for any worlds that are altered, destroyed, corrupted or damaged through the use of the {@link DataFixerUtils} because of improper configuration, mispellings, incorrect syntax, etc.</b>
 * @author i509
 *
 */
public interface DataFixerUtils {
    
    public static final DataFixerUtils INSTANCE = FabricDataFixerImpl.INSTANCE;
    
    public static final BiFunction<Integer,Schema,Schema> SCHEMA_IDENTIFIER_NORMALIZE = SchemaIdentifierNormalize::new;
    public static final BiFunction<Integer,Schema,Schema> SCHEMA_EMPTY = Schema::new;
    
    /**
     * 
     * @param modid The modid of the mod registering this DataFixer
     * @param runtimeDataVersion the current dataversion of the mod being ran.
     * @param datafixer The DataFixer to register
     * @throws CrashException if a DataFixer is registered while the Client or Dedicated/Integrated Server is running.
     * @return The inputted DataFixer
     */
    public DataFixer registerFixer(String modid, int runtimeDataVersion, DataFixer datafixer);
    
    public boolean isLocked();
}
