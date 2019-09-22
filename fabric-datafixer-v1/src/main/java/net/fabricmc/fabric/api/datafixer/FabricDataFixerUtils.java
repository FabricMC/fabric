package net.fabricmc.fabric.api.datafixer;

import java.util.function.BiFunction;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;

import net.fabricmc.fabric.datafixer.mixin.util.SchemasAccessor;
import net.fabricmc.fabric.impl.datafixer.FabricDataFixerImpl;
import net.minecraft.util.crash.CrashException;

/**
 * This class is what registers and creates DataFixers for the game.
 * <p><b>Please take extreme caution when using these tools as DataFixers directly interface with the world saves and may corrupt world saves.</b></p>
 * @disclaimer <b>I, i509VCB and the Fabric Team do not take an responsibility for any worlds that are altered, destroyed, corrupted or damaged through the use of the {@link FabricDataFixerUtils} because of improper configuration, mispellings, incorrect syntax, etc.</b>
 * @author i509
 *
 */
public class FabricDataFixerUtils {
    
    static boolean LOCKED = false;
    
    public static final BiFunction<Integer,Schema,Schema> IDENTIFIER_NORMALIZE = SchemasAccessor.getIdentNormalize();
    public static final BiFunction<Integer,Schema,Schema> EMPTY = SchemasAccessor.getEmpty();
    
    public static boolean isLocked() {
        return LOCKED;
    }
    
    /**
     * 
     * @param modid The modid of the mod registering this DataFixer
     * @param runtimeDataVersion the current dataversion of the mod being ran.
     * @param datafixer The DataFixer to register
     * @throws CrashException if a DataFixer is registered while the Dedicated/Integrated Server is running.
     */
    public static DataFixer registerFixer(final String modid, final int runtimeDataVersion, final DataFixer datafixer) {
        return FabricDataFixerImpl.INSTANCE.registerFixer(modid, runtimeDataVersion, datafixer);
    }
}
