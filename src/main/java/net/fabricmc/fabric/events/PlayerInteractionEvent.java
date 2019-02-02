/*
 * Copyright (c) 2016, 2017, 2018 FabricMC
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

package net.fabricmc.fabric.events;

/**
 * This is a class for INTERACTION EVENTS (think left-clicking/right-clicking). For block placement/break
 * events, look elsewhere - this just handles the player!
 *
 * These hook in BEFORE the spectator checks, so make sure to check for the player's game mode as well!
 *
 * In general, the events return an ActionResult with the following side effects:
 * - SUCCESS cancels further processing and, on the client, sends a packet to the server.
 * - PASS falls back to further processing.
 * - FAIL cancels further processing and does not send a packet to the server.
 *
 * CURRENT LIMITATIONS:
 *
 * - INTERACT_BLOCK/INTERACT_ITEM do not expect the ItemStack instance in the player's held hand to change!
 *   If you must do that, consider returning an ActionResult.SUCCESS and re-emitting the event in some manner!
 * - ATTACK_BLOCK does not let you control the packet sending process yet.
 */
@Deprecated
public final class PlayerInteractionEvent {
}
