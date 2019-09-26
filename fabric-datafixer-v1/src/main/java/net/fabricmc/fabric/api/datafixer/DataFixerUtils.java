package net.fabricmc.fabric.api.datafixer;

import java.util.Optional;

import com.mojang.datafixers.DataFixer;

import net.fabricmc.fabric.impl.datafixer.FabricDataFixerImpl;
import net.minecraft.nbt.CompoundTag;

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
     * Registers a DataFixer
     * @param modid The modid of the mod registering this DataFixer
     * @param runtimeDataVersion the current dataversion of the mod being ran.
     * @param datafixer The DataFixer to register
     * @return The inputted DataFixer
     */
    public DataFixer registerFixer(String modid, int runtimeDataVersion, DataFixer datafixer);
    
    /**
     * Gets the DataFixer registered under a mod.
     * @param modid The Modid which the DataFixer was registered under.
     * @return An optional, which may contain a DataFixer if a mod has registered a DataFixer.
     */
    public Optional<DataFixer> getDataFixer(String modid);
    
    /**
     * Retrieves the DataVersion registered under a modid.
     * @param compoundTag The CompoundTag to check
     * @param modid The modid to check.
     * @return The DataVersion stored for the mod or 0 if no DataVersion or mod is present. 
     */
    public int getModDataVersion(CompoundTag compoundTag, String modid);
    
    public boolean isLocked();
}
