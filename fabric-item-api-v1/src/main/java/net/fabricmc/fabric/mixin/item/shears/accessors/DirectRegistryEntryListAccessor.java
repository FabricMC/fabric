package net.fabricmc.fabric.mixin.item.shears.accessors;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Set;

@Mixin(RegistryEntryList.Direct.class)
public interface DirectRegistryEntryListAccessor<T> {
    @Accessor
    List<RegistryEntry<T>> getEntries();

    @Accessor
    void setEntries(List<RegistryEntry<T>> entries);

    @Accessor
    void setEntrySet(Set<RegistryEntry<T>> entrySet);
}
