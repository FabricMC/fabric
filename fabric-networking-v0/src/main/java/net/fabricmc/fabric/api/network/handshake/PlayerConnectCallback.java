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
package net.fabricmc.fabric.api.network.handshake;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;

public interface PlayerConnectCallback {

    static final Map<String, Event<PlayerConnectCallback>> modMap = new HashMap<String, Event<PlayerConnectCallback>>();
    
    /**
     * Gets the PlayerConnectCallback for the modid or creates an event for it.
     * @param modid The Modid of the mod to get the event of
     * @return The event from the modid
     */
    static Event<PlayerConnectCallback> getEventOrCreate(String modid) {
        return modMap.computeIfAbsent(modid, (key) -> createEvent());
    }
    
    /**
     * Gets the PlayerConnectCallback wrapped inside of an Optional for the modid.
     * @param modid The Modid of the mod to get the event of
     * @return The event from the modid
     */
    static Optional<Event<PlayerConnectCallback>> getEvent(String modid) {
        return Optional.ofNullable(modMap.get(modid));
    }
    
    
    /**
     * Creates an empty event for a mod. Do not use, for implementation only.
     */
    static Event<PlayerConnectCallback> createEvent() {
        return EventFactory.createArrayBacked(PlayerConnectCallback.class, (callbacks) -> (ctx) -> {
            for (PlayerConnectCallback callback : callbacks) {
                
                TypedActionResult<Text> result = callback.onHandshake(ctx);
                if(result.getResult()==ActionResult.FAIL) {
                    return result;
                }
            }
            return new TypedActionResult<Text>(ActionResult.SUCCESS, null);
        });
    }
    
    /**
     * Checks if any mods have Handshake Handler's registered. Do not use, for implementation only.
     * @return
     */
    static boolean hasRegisteredAny() {
        return !modMap.isEmpty();
    }
    
    /**
     * Returns a result from a Handshake handler. This may return a null value but will always return a not-null ActionResult inside.
     * @param clientProvidedVersion The version of the mod installed that is provided by the client.
     * @return Text if the handshake failed. Recommended to make this {@link LiteralText} so fabric clients that are missing your mod don't receive untranslated text. 
     */
    TypedActionResult<Text> onHandshake(String clientProvidedVersion);

}
