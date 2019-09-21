package net.fabricmc.fabric.impl.datafixer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;

import net.fabricmc.fabric.api.util.NbtType;
import net.fabricmc.fabric.impl.datafixer.FabricDataFixerInvoker.DataFixerEntry;
import net.minecraft.datafixers.DataFixTypes;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

public class FabricDataFixerInvoker {
    
    Map<String, DataFixerEntry> MOD_FIXERS = new HashMap<String, DataFixerEntry>();
    
    private FabricDataFixerInvoker() {}
    
    public static final FabricDataFixerInvoker INSTANCE = new FabricDataFixerInvoker();;
    
    public void registerFixer(String modid, int runtimeDataVersion, DataFixer datafixer) {
        MOD_FIXERS.put(modid, new DataFixerEntry(datafixer, runtimeDataVersion));
    }
    
    public static CompoundTag updateWithAllFixers(DataFixer dataFixer_1, DataFixTypes dataFixTypes_1, CompoundTag compoundTag_1, int dyanamicDataVersion, int runtimeDataVersion) {
        Iterator<Entry<String, DataFixerEntry>> iterator = INSTANCE.MOD_FIXERS.entrySet().iterator();
        CompoundTag currentTag = compoundTag_1;
        
        while(iterator.hasNext()) {
            Entry<String, DataFixerEntry> entry = iterator.next();
            try {
                
                String currentModid = entry.getKey();
                
                int modidCurrentDynamicVersion = currentTag.containsKey(currentModid + "_DataVersion", NbtType.NUMBER) ? currentTag.getInt(currentModid + "_DataVersion") : -1;
                
                DataFixerEntry dataFixer = entry.getValue();
                currentTag = (CompoundTag) dataFixer.modFixer.update(dataFixTypes_1.getTypeReference(), new Dynamic<Tag>(NbtOps.INSTANCE, currentTag), modidCurrentDynamicVersion, dataFixer.runtimeDataVersion).getValue();
                
            } catch (Throwable t) {
                // Something went horribly wrong, kill the game to prevent any/further corruption
                CrashReport report = CrashReport.create(t, "Exception while DataFixing");
                
                CrashReportSection section1 = report.addElement("Current DataFixer");
                section1.add("Mod which registered selected DataFixer", entry.getKey());
                
                CrashReportSection section2 = report.addElement("CompoundTag being fixed");
                section2.add("Original CompoundTag before fix", compoundTag_1.asString());
                section2.add("CompoundTag state before exception", currentTag.asString());
                
                throw new CrashException(report);
            }
        }
        
        return currentTag;
    }

    public static void addFixerVersions(CompoundTag compoundTag_1) {
        Iterator<Entry<String, DataFixerEntry>> iterator = INSTANCE.MOD_FIXERS.entrySet().iterator();
        
        while(iterator.hasNext()) {
            Entry<String, DataFixerEntry> entry = iterator.next();
            compoundTag_1.putInt(entry.getKey() + "_DataVersion", entry.getValue().runtimeDataVersion);
        }
    }

    class DataFixerEntry {
        public DataFixer modFixer;
        public int runtimeDataVersion;
        
        public DataFixerEntry(DataFixer fix, int runtimeDataVersion) {
            this.modFixer = fix;
            this.runtimeDataVersion = runtimeDataVersion;
        }
    }
}
