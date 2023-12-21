package net.fabricmc.fabric.mixin.item.shears.accessors;

import java.util.List;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;

@Mixin(RegistryEntryList.Direct.class)
public interface DirectRegistryEntryListAccessor<T> {
    @Accessor
    List<RegistryEntry<T>> getEntries();

    @Accessor @Mutable
    void setEntries(List<RegistryEntry<T>> entries);

    @Accessor
    void setEntrySet(Set<RegistryEntry<T>> entrySet);
}
