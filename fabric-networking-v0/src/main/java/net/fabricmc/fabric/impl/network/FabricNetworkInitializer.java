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

package net.fabricmc.fabric.impl.network;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.util.NbtType;
import net.fabricmc.fabric.impl.network.handshake.HandshakeModHandlerImpl;
import net.fabricmc.fabric.impl.network.login.S2CLoginHandshakeCallback;
import net.minecraft.client.network.packet.LoginDisconnectS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class FabricNetworkInitializer implements ModInitializer {
    
    public static final TranslatableText MISMATCH_VERSION_TEXT = new TranslatableText("fabric-networking-v0.hello.mismatch", FabricHelloPacketBuilder.MAJOR_VERSION, FabricHelloPacketBuilder.MINOR_VERSION);
    
    /*
    
    @Deprecated
    public void toRemove() {
        S2CLoginHandshakeCallback.EVENT.register(queue -> {
            queue.sendPacket(FabricHelloPacketBuilder.ID, FabricHelloPacketBuilder.buildHelloPacket(), (handler, connection, id, responseBuf) -> {
                CompoundTag response;
                try {
                    response = responseBuf.readCompoundTag();
                } catch (Throwable e) {
                    response = null;
                }
                
                boolean shouldKick = !ServerRequiresModRegistryImpl.INSTANCE.REQUIRED_MODS.isEmpty();

                if (response != null && response.containsKey("majorVersion", NbtType.NUMBER) && response.containsKey("minorVersion", NbtType.NUMBER)) {
                    LOGGER.debug("Read compound tag - connected to a Fabric client!");
                    
                    if(!shouldKick) {
                        return; // No mods installed don't go on.
                    }
                    
                    int versionMajor = response.getInt("majorVersion");
                    int versionMinor = response.getInt("minorVersion");
                    
                    if((versionMajor != FabricHelloPacketBuilder.MAJOR_VERSION || versionMinor != FabricHelloPacketBuilder.MINOR_VERSION)) {
                        LOGGER.warn("Kicked client because of mismatched fabric:hello version, expected major version: " + FabricHelloPacketBuilder.MAJOR_VERSION + " and minor version: " + FabricHelloPacketBuilder.MINOR_VERSION);
                        connection.send(new LoginDisconnectS2CPacket(MISMATCH_VERSION_TEXT));
                        return;
                    }

                    Map<String, String> mismatchedVersion = new HashMap<String, String>();
                    Map<String, String> missingMod = new HashMap<String, String>();
                    
                    if(shouldKick) { // If one mod requires a client mod then this flag is true
                        LOGGER.debug("Checking if client has all mods required by server.");
                        
                        CompoundTag modsTag = response.getCompound("mods");
                        
                        Iterator<Entry<String, String>> iterator = ServerRequiresModRegistryImpl.INSTANCE.REQUIRED_MODS.entrySet().iterator();
                        
                        while(iterator.hasNext()) {
                            Entry<String, String> entry = iterator.next();
                            
                            String modid = entry.getKey();
                            String version = entry.getValue();
                            
                            if(!modsTag.containsKey(modid, NbtType.STRING)) { // Missing mod from client
                                missingMod.put(modid, version);
                                continue; // No need for extra logic if it's already missing.
                            } else if ((modsTag.getString(modid) != ServerRequiresModRegistryImpl.INSTANCE.REQUIRED_MODS.get(modid)) && modsTag.containsKey(modid, NbtType.STRING)) {
                                mismatchedVersion.put(modid, ServerRequiresModRegistryImpl.INSTANCE.REQUIRED_MODS.get(modid)); // We add the version to the map that the server requires.
                            }
                        }
                    }
                    
                    if(!missingMod.isEmpty() || !mismatchedVersion.isEmpty()) {
                        connection.send(new LoginDisconnectS2CPacket(buildDisconnectText(mismatchedVersion, missingMod)));
                    }
                    
                } else {
                    LOGGER.debug("Could not read compound tag - probably not a Fabric client!");
                       if(shouldKick) {
                            LOGGER.debug("Client is missing " + ServerRequiresModRegistryImpl.INSTANCE.REQUIRED_MODS.size() + " mod(s)");
                            LOGGER.warn("Client is missing mods, disconnecting");
                            connection.send(new LoginDisconnectS2CPacket(buildDisconnectTextVanilla()));
                       } else {
                           LOGGER.debug("Client is vanilla, however nothing on the server requires a client mod so letting them in anyways.");
                       }
                }
            });
        });
    }
    */
    
    @Override
    public void onInitialize() {
        S2CLoginHandshakeCallback.EVENT.register(queue -> {
            queue.sendPacket(FabricHelloPacketBuilder.ID, FabricHelloPacketBuilder.buildHelloPacket(), (handler, connection, id, responseBuf) -> {
                HandshakeModHandlerImpl.handlePacket(connection, id, responseBuf);
            });
        });
    }
    
    @Deprecated // To be removed and replaced
    private Text buildDisconnectText(Map<String, String> missingMod, Map<String, String> mismatchVersions) {
        Text message = new LiteralText("");
        
        if(!missingMod.isEmpty()) {
            message.append(new TranslatableText("fabric-networking-v0.missing.amount", missingMod.size()));
            message.append(new TranslatableText("fabric-networking-v0.missing.requires"));
            
            Iterator<Entry<String, String>> missingIterator = missingMod.entrySet().iterator();
                    
            while(missingIterator.hasNext()) {
                Entry<String, String> missingEntry = missingIterator.next();
                
                message.append(missingEntry.getKey());
                
                message.append(" ver- " + missingEntry.getValue());
                
                if(missingIterator.hasNext()) {
                    message.append(", ");
                } else {
                    message.append(".");
                }
                    
            }
        
        }
        
        if(!mismatchVersions.isEmpty()) {
            message.append(new TranslatableText("fabric-networking-v0.mismatch_spaced"));
            
            Iterator<Entry<String, String>> mismatchIterator = mismatchVersions.entrySet().iterator();
            
            while(mismatchIterator.hasNext()) {
                Entry<String, String> mismatchEntry = mismatchIterator.next();
                
                message.append(mismatchEntry.getKey() + " - " + mismatchEntry.getValue());
                
                if(mismatchIterator.hasNext()) {
                    message.append(", ");
                } else {
                    message.append(".");
                }
            }
        }
        return message;
    }
}
