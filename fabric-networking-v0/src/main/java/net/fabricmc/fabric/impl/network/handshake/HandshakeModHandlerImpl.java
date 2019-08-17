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
package net.fabricmc.fabric.impl.network.handshake;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.network.handshake.PlayerConnectCallback;
import net.fabricmc.fabric.api.util.NbtType;
import net.fabricmc.fabric.impl.network.FabricHelloPacketBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.network.packet.LoginDisconnectS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.TypedActionResult;

public class HandshakeModHandlerImpl {

    private static Map<String, Boolean> shouldHandshake = new HashMap<String, Boolean>();

    private static final Predicate<ModContainer> SHOULD_HANDSHAKE = (mod) -> {
        Optional<ModContainer> cont = FabricLoader.getInstance().getModContainer(mod.getMetadata().getId()); // Just
                                                                                                             // verify
                                                                                                             // incase.

        if (cont.isPresent()) {
            try {
                ModMetadata meta = cont.get().getMetadata();

                boolean shouldHandshakeB = true;

                shouldHandshake.put(mod.getMetadata().getId(), true);

                if (meta.getCustomElement("shouldHandshake") != null
                        && meta.getCustomElement("shouldHandshake").isJsonPrimitive()) {

                    shouldHandshakeB = meta.getCustomElement("shouldHandshake").getAsBoolean();

                }
                if (!shouldHandshakeB) {
                    return false;
                    // handshakeList.put(mod.getMetadata().getId(), false); // Return false to
                    // remove from filter.
                } else {
                    return true;
                    // shouldHandshake.put(mod.getMetadata().getId(), true);

                    /*
                     * Optional<Event<PlayerConnectCallback>> events =
                     * PlayerConnectCallback.getEvent(mod.getMetadata().getId());
                     * 
                     * if(events.isPresent()) { ActionResult result =
                     * events.get().invoker().onHandshake(mod.getMetadata().getVersion().
                     * getFriendlyString()); }
                     */
                }
            } catch (Throwable t) { // Fails to do this then go ahead and make it require anyways.
                return true;
                // shouldHandshake.put(mod.getMetadata().getId(), true);
            }
        }
        return true;
    };

    static {
        // This whole mess just tells HandshakeHandler which mods have handlers and which should do literal version checking.
        FabricLoader.getInstance().getAllMods().stream().filter(SHOULD_HANDSHAKE).forEach(mod -> {
            
            Optional<ModContainer> cont = FabricLoader.getInstance().getModContainer(mod.getMetadata().getId());

            if (cont.isPresent()) { // Still gotta check incase.
                ModMetadata meta = cont.get().getMetadata();
                String modid = meta.getId();
                
                Optional<Event<PlayerConnectCallback>> eventOp = PlayerConnectCallback.getEvent(modid);
                
                if(eventOp.isPresent()) {
                    shouldHandshake.put(mod.getMetadata().getId(), true);
                } else {
                    shouldHandshake.put(mod.getMetadata().getId(), false); // False value means no handlers, check version literally.
                }
            }
        });

        /*
         * Optional<Event<PlayerConnectCallback>> events =
         * PlayerConnectCallback.getEvent(mod.getMetadata().getId());
         * 
         * if(events.isPresent()) { ActionResult result =
         * events.get().invoker().onHandshake(mod.getMetadata().getVersion().
         * getFriendlyString()); }
         */
    }

    public static final TranslatableText MISMATCH_VERSION_TEXT = new TranslatableText("fabric-networking-v0.hello.mismatch", FabricHelloPacketBuilder.MAJOR_VERSION, FabricHelloPacketBuilder.MINOR_VERSION);
    
    protected static final Logger LOGGER = LogManager.getLogger();

    public static void handlePacket(ClientConnection connection, Identifier id, PacketByteBuf responseBuf) {

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && !connection.isLocal()) {
            // Don't do this on the client's own IntegratedServer, thats why we have a isn't local flag to detect singleplayer clients.
            return;
        }

        if (shouldHandshake.isEmpty()) { // Empty so no mods require a handshake.
            return;
        }

        // start

        CompoundTag response;
        try {
            response = responseBuf.readCompoundTag();
        } catch (Throwable e) {
            connection.send(new LoginDisconnectS2CPacket(buildDisconnectTextVanilla())); // I am Vanilla / Forgethonk
            return;
        }

        if (response != null && response.containsKey("majorVersion", NbtType.NUMBER)
                && response.containsKey("minorVersion", NbtType.NUMBER)) {
            LOGGER.debug("Read compound tag - connected to a Fabric client!");
        } else {
            LOGGER.warn("Recived a fabric:hello packet with no version tag");
            connection.send(
                    new LoginDisconnectS2CPacket(new TranslatableText("fabric-networking-v0.hello.missing.version")));
            return;
        }

        int versionMajor = response.getInt("majorVersion");
        int versionMinor = response.getInt("minorVersion");

        if ((versionMajor != FabricHelloPacketBuilder.MAJOR_VERSION
                || versionMinor != FabricHelloPacketBuilder.MINOR_VERSION)) {
            LOGGER.warn("Kicked client because of mismatched fabric:hello version, expected major version: "
                    + FabricHelloPacketBuilder.MAJOR_VERSION + " and minor version: "
                    + FabricHelloPacketBuilder.MINOR_VERSION);
            connection.send(new LoginDisconnectS2CPacket(MISMATCH_VERSION_TEXT)); // End here
            return;
        }

        CompoundTag modTag = response.getCompound("mods");

        if (modTag == null) { // Packet can have empty mods compound but never null
            connection.send(
                    new LoginDisconnectS2CPacket(new TranslatableText("fabric-networking-v0.hello.missing.nullmods")));
            return;
        }

        List<String> compoundKeys = new ArrayList<String>();

        compoundKeys.addAll(modTag.getKeys());

        Map<String, String> clientMods = IntStream.range(0, compoundKeys.size()).collect(HashMap::new,
                (map, keyPos) -> {
                    map.put(compoundKeys.get(keyPos), modTag.getString(compoundKeys.get(keyPos)));

                }, HashMap::putAll);

        Iterator<Entry<String, Boolean>> it = shouldHandshake.entrySet().iterator();

        while (it.hasNext()) {
            Entry<String, Boolean> entry = it.next();

            String modid = entry.getKey();

            if (clientMods.containsKey(modid)) {
                if (entry.getValue().booleanValue()) { // If true, this mod has a handler otherwise check version literally.
                    TypedActionResult<Text> result = PlayerConnectCallback.getEvent(modid).get().invoker().onHandshake(clientMods.get(modid));
                    
                    if(result.getResult() == ActionResult.FAIL) {
                        
                        if(result.getValue() != null) {
                            // TODO add result to failure list
                        } else {
                            // TODO add default result to failure list.
                        }
                        continue;
                    }
                    
                } else {
                    
                    
                    String version = clientMods.get(modid);
                    
                    Optional<ModContainer> op = FabricLoader.getInstance().getModContainer(modid);
                    
                    if (op.isPresent()) {
                        if (op.get().getMetadata().getVersion().getFriendlyString() == version) {
                            continue;
                        } else {
                            // Failed, add a version mismatch map.
                        }
                    }
                }
            } else {
                // TODO failed check, mod is missing.
            }
        }
    }

    private static ActionResult defaultVersionCheck(String modid, String version) {
        
        
        
        return null;
    }

    private static LiteralText buildDisconnectTextVanilla() { // TODO: When config API comes by, possibly add an option for server owners to specify a link they can grab the mods running on the server.
        return new LiteralText("This server requires you install Fabric to join.");
    }
}
