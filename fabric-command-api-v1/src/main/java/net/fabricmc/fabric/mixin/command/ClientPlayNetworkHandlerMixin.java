package net.fabricmc.fabric.mixin.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import net.fabricmc.fabric.api.command.v1.ClientCommandManager;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Shadow
    @Final
    private ClientCommandSource commandSource;
    @Shadow
    @Final
    private CommandDispatcher<CommandSource> commandDispatcher;

    @Inject(method = "onCommandTree", at = @At("RETURN"))
    private void onOnCommandTree(CallbackInfo callbackInfo) {
        ClientCommandManager.INSTANCE.getDispatcher().getRoot().getChildren().forEach(node -> {
            if (node.canUse(commandSource)) {
                commandDispatcher.getRoot().addChild(nonCanNotUse(node, commandSource));
            }
        });
    }

    private static <T> CommandNode<T> nonCanNotUse(CommandNode<T> root, T source) {
        CommandNode<T> newRoot = root.createBuilder().build();

        for (CommandNode<T> node : root.getChildren()) {
            if (node.canUse(source)) {
                newRoot.addChild(nonCanNotUse(node, source));
            }
        }

        return newRoot;
    }
}
