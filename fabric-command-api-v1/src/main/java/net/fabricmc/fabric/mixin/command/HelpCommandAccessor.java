package net.fabricmc.fabric.mixin.command;

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.server.command.HelpCommand;

@Mixin(HelpCommand.class)
public interface HelpCommandAccessor {
	@Accessor("FAILED_EXCEPTION")
	static SimpleCommandExceptionType getFailedException() {
		throw new AssertionError("mixin");
	}
}
