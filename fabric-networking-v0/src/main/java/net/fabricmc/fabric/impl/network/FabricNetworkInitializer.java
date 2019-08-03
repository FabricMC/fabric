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
import net.fabricmc.fabric.impl.network.login.S2CLoginHandshakeCallback;
import net.minecraft.client.network.packet.LoginDisconnectS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class FabricNetworkInitializer implements ModInitializer {
	protected static final Logger LOGGER = LogManager.getLogger();
    
    public static final LiteralText MISMATCH_VERSION_TEXT = new LiteralText("Mismatched hello packet version, expected version: " + FabricHelloPacketBuilder.VERSION_MAJOR + "." + FabricHelloPacketBuilder.VERSION_MINOR);
    
    @Override
    public void onInitialize() {
        S2CLoginHandshakeCallback.EVENT.register(queue -> {
            queue.sendPacket(FabricHelloPacketBuilder.ID, FabricHelloPacketBuilder.buildHelloPacket(), (handler, connection, id, responseBuf) -> {
                CompoundTag response;
                try {
                    response = responseBuf.readCompoundTag();
                } catch (Throwable e) {
                    response = null;
                }
                
                boolean shouldKick = !ServerRequiresModRegistryImpl.INSTANCE.REQUIRED_MODS.isEmpty();

                if (response != null && response.containsKey("versionMajor", NbtType.NUMBER) && response.containsKey("versionMinor", NbtType.NUMBER)) {
                    LOGGER.debug("Read compound tag - connected to a Fabric client!");
                    
                    if(!shouldKick) {
                        return; // No mods installed don't go on.
                    }
                    
                    int versionMajor = response.getInt("versionMajor");
                    int versionMinor = response.getInt("versionMinor");
                    
                    if((versionMajor != FabricHelloPacketBuilder.VERSION_MAJOR || versionMinor != FabricHelloPacketBuilder.VERSION_MINOR)) {
                        LOGGER.warn("Kicked client because of mismatched fabric:hello version, expected major version: " + FabricHelloPacketBuilder.VERSION_MAJOR + " and minor version: " + FabricHelloPacketBuilder.VERSION_MINOR);
                        connection.send(new LoginDisconnectS2CPacket(MISMATCH_VERSION_TEXT));
                        return;
                    }

                    boolean canJoin = true;
                    int missingCounter = 0;

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
                                missingCounter++;
                                canJoin=false;
                                continue; // No need for extra logic if it's already missing.
                            } else if ((modsTag.getString("version") != ServerRequiresModRegistryImpl.INSTANCE.REQUIRED_MODS.get(modid)) && modsTag.containsKey(modid, NbtType.STRING)) {
                                mismatchedVersion.put(modid, ServerRequiresModRegistryImpl.INSTANCE.REQUIRED_MODS.get(modid)); // We add the version to the map that the server requires.
                            }
                        }
                    }
                    
                    if(!canJoin) {
                        connection.send(new LoginDisconnectS2CPacket(buildDisconnectTextMissing(missingCounter, mismatchedVersion, missingMod)));
                    }
                    
                } else {
                    LOGGER.debug("Could not read compound tag - probably not a Fabric client!");
                       if(shouldKick) {
                            LOGGER.debug("Client is missing " + ServerRequiresModRegistryImpl.INSTANCE.REQUIRED_MODS.size() + " mod(s)");
                            LOGGER.warn("Client is missing mods, disconnecting");
                            connection.send(new LoginDisconnectS2CPacket(buildDisconnectTextMissingVanilla()));
                       } else {
                           LOGGER.debug("Client is vanilla, however nothing on the server requires a client mod so letting them in anyways.");
                       }
                }
            });
        });
    }
    
    private Text buildDisconnectTextMissing(int missingCounter, Map<String, String> missingMod, Map<String, String> mismatchVersions) {
        Text message = new LiteralText("");
        
        if(!missingMod.isEmpty()) {
            message.append("You are missing " + missingCounter + " mod(s). ");
            message.append("This server requires you install the following mod(s): ");
            
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
            message.append(" These mod(s) have a version mismatch: ");
            
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
    
    private Text buildDisconnectTextMissingVanilla() { // TODO: When config API comes by, possibly add an option for server owners to specify a link they can grab the mods running on the server.
        
        Text message = new LiteralText("");
        
        Map<String, String> required = ServerRequiresModRegistryImpl.INSTANCE.REQUIRED_MODS;
        
        message.append("This server requires you install Fabric to join. ");
        message.append("You are missing " + required.size() + " mod(s). ");
        
        Iterator<Entry<String, String>> missingIterator = required.entrySet().iterator();
        
        while(missingIterator.hasNext()) {
            Entry<String, String> missingEntry = missingIterator.next();
            
            message.append(missingEntry.getKey());
            
            message.append(" Version: " + missingEntry.getValue());
            
            if(missingIterator.hasNext()) {
                message.append(", ");
            } else {
                message.append(".");
            } 
        }
        
        return message;
    }
}
