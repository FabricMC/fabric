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

package net.fabricmc.fabric.mixin.command;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import net.fabricmc.fabric.api.command.v1.ClientCommandManager;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    @Shadow
    @Final
    private ClientPlayNetworkHandler networkHandler;

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String message, CallbackInfo callbackInfo) {
        if (message.length() > 2 && message.charAt(0) == '/') {
            StringReader reader = new StringReader(message);
            reader.setCursor(1);
            ParseResults<CommandSource> parse = 
				ClientCommandManager.INSTANCE.getDispatcher().parse(reader, networkHandler.getCommandSource());
			
            if (parse.getReader().canRead()) {//try server
                StringReader reader1 = new StringReader(message);
                reader1.setCursor(1);
                parse = networkHandler.getCommandDispatcher().parse(reader1, networkHandler.getCommandSource());
                if (parse.getReader().canRead()) {//print error messages
                    ClientCommandManager.INSTANCE.execute(networkHandler.getCommandDispatcher(), parse);

                    callbackInfo.cancel();
                }
            } else {//excute command
                ClientCommandManager.INSTANCE.execute(ClientCommandManager.INSTANCE.getDispatcher(), parse);

                callbackInfo.cancel();
            }
        }
    }
}
