package net.fabricmc.fabric.mixin.gametest;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.SharedConstants;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TestCommand;

@Mixin(CommandManager.class)
public abstract class CommandManagerMixin {
	@Shadow
	@Final
	private CommandDispatcher<ServerCommandSource> dispatcher;

	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/WorldBorderCommand;register(Lcom/mojang/brigadier/CommandDispatcher;)V", shift = At.Shift.AFTER))
	private void construct(CommandManager.RegistrationEnvironment environment, CallbackInfo info) {
		// Will be registered by vanilla if enabled.
		if (!SharedConstants.isDevelopment) {
			TestCommand.register(this.dispatcher);
		}
	}
}
