package net.fabricmc.fabric.api.datafixer;

import java.util.Optional;
import java.util.function.BiFunction;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;

import net.fabricmc.fabric.impl.datafixer.FabricDataFixerImpl;
import net.minecraft.SharedConstants;
import net.minecraft.datafixers.Schemas;
import net.minecraft.datafixers.schemas.SchemaIdentifierNormalize;
import net.minecraft.nbt.CompoundTag;
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
    
    /**
     * 
     * @param modid The modid of the mod registering this DataFixer
     * @param runtimeDataVersion the current dataversion of the mod being ran.
     * @param datafixer The DataFixer to register
     * @throws CrashException if a DataFixer is registered while the Client or Dedicated/Integrated Server is running.
     * @return The inputted DataFixer
     */
    public DataFixer registerFixer(String modid, int runtimeDataVersion, DataFixer datafixer);
    
    public Optional<DataFixer> getDataFixer(String modid);
    
    /**
     * Retrieves the DataVersion registered under a modid.
     * @param compoundTag The CompoundTag to check
     * @param modid The modid to check.
     * @return The DataVersion stored for the mod or -1 if no DataVersion is present
     */
    public int getModDataVersion(CompoundTag compoundTag, String modid);
    
    public boolean isLocked();
}
