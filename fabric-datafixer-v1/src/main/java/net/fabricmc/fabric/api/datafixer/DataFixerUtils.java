/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
