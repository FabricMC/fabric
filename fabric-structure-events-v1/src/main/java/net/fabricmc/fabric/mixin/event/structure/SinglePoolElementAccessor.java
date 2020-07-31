package net.fabricmc.fabric.mixin.event.structure;

import com.mojang.datafixers.util.Either;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.Structure;
import net.minecraft.util.Identifier;

@Mixin(SinglePoolElement.class)
public interface SinglePoolElementAccessor {
	@Accessor("field_24015")
	Either<Identifier, Structure> structureId();
}
