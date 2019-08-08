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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.network.handshake.FailureReason;
import net.fabricmc.fabric.api.network.handshake.HandshakeContext;
import net.fabricmc.fabric.api.network.handshake.PlayerConnectCallback;
import net.fabricmc.fabric.api.util.NbtType;
import net.fabricmc.fabric.impl.network.FabricHelloPacketBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.network.packet.LoginDisconnectS2CPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;

public class HandshakeModHandlerImpl {

    public static final TranslatableText MISMATCH_VERSION_TEXT = new TranslatableText("fabric-networking-v0.hello.mismatch", FabricHelloPacketBuilder.MAJOR_VERSION, FabricHelloPacketBuilder.MINOR_VERSION);
    protected static final Logger LOGGER = LogManager.getLogger();

    public static void handlePacket(ClientConnection connection, Identifier id, PacketByteBuf responseBuf) {

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) { // Don't do this on client's IntegratedServer
            return;
        }

        if (PlayerConnectCallback.modMap.isEmpty()) { // Empty so go on anyways
            return;
        }

        // start

        CompoundTag response;
        try {
            response = responseBuf.readCompoundTag();
        } catch (Throwable e) {
            connection.send(new LoginDisconnectS2CPacket(buildDisconnectTextVanilla())); // I Am Vanilla / Forgethonk
            return;
        }
        
        if (response != null && response.containsKey("majorVersion", NbtType.NUMBER) && response.containsKey("minorVersion", NbtType.NUMBER)) {
            LOGGER.debug("Read compound tag - connected to a Fabric client!");
        } else {
            LOGGER.warn("Recived a fabric:hello packet with no version tag");
            connection.send(new LoginDisconnectS2CPacket(new TranslatableText("fabric-networking-v0.hello.missing.version")));
            return;
        }
        
        int versionMajor = response.getInt("majorVersion");
        int versionMinor = response.getInt("minorVersion");
        
        if((versionMajor != FabricHelloPacketBuilder.MAJOR_VERSION || versionMinor != FabricHelloPacketBuilder.MINOR_VERSION)) {
            LOGGER.warn("Kicked client because of mismatched fabric:hello version, expected major version: " + FabricHelloPacketBuilder.MAJOR_VERSION + " and minor version: " + FabricHelloPacketBuilder.MINOR_VERSION);
            connection.send(new LoginDisconnectS2CPacket(MISMATCH_VERSION_TEXT)); // End here
            return;
        }

        CompoundTag modTag = response.getCompound("mods");
        
        if (modTag == null) { // Packet can have empty mods compound but never null
            connection.send(new LoginDisconnectS2CPacket(new TranslatableText("fabric-networking-v0.hello.missing.nullmods")));
            return;
        }

        List<String> compoundKeys = new ArrayList<String>();

        compoundKeys.addAll(modTag.getKeys());

        Map<String, String> clientMods = IntStream.range(0, compoundKeys.size()).collect(HashMap::new,
                (map, keyPos) -> {
                    map.put(compoundKeys.get(keyPos), modTag.getString(compoundKeys.get(keyPos)));
                }, HashMap::putAll);

        // end

        Set<String> modids = PlayerConnectCallback.modMap.keySet();

        Iterator<String> iterator = modids.iterator();

        while (iterator.hasNext()) {
            String modid = iterator.next();

            ActionResult result;

            HandshakeContext ctx = new HandshakeContextImpl(clientMods);
            result = PlayerConnectCallback.getEvent(modid).get().invoker().onHandshake(ctx);
            if (result == ActionResult.FAIL) {
                ctx.getFailureReasons();
            }

            if (result == ActionResult.FAIL) {
                // TODO unknown failure reason
            }
        }

    }
    
    private static LiteralText buildDisconnectTextVanilla() { // TODO: When config API comes by, possibly add an option for server owners to specify a link they can grab the mods running on the server.
        return new LiteralText("This server requires you install Fabric to join.");
    }

    public static class HandshakeContextImpl implements HandshakeContext {

        private Map<String, String> clientIds;
        private List<FailureReason> failureReasons;

        HandshakeContextImpl(Map<String, String> clientIds) {
            this.clientIds = clientIds;
        }

        @Override
        public FabricLoader getLoader() {
            return FabricLoader.getInstance();
        }

        @Override
        public Collection<String> getClientModIDs() {
            return ImmutableSet.copyOf(clientIds.keySet());
        }

        public String getModIDVersion(String modid) {
            return clientIds.get(modid);
        }

        @Override
        public void fail(FailureReason reason) {
            failureReasons.add(reason);
        }

        @Override
        public Collection<FailureReason> getFailureReasons() {
            return ImmutableList.copyOf(failureReasons);
        }

    }
}
