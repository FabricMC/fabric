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

/**
 * The fabric networking handshake API.
 *
 * <p>This API allows mods to reject clients based on their mod presence.
 *
 * <p>Mods can register handshake mod version mismatch reporters. If any of the
 * reporters reported a mismatch, the connection is rejected.
 *
 * <p>Mods can have a "fabric-networking-handshake" section in their custom values,
 * which has the following optional suboptions:
 * <ul><li>"requireExactVersion": if true, it will check for exact version match on
 * client and server; otherwise, it will check for mod presence on client.</li>
 * <li>"versionRange": specifies an accepted remote version range for this mod.
 * Since this is more specific than the exact version requirement, it is pointless
 * to declare both.</li>
 * </ul>
 */

package net.fabricmc.fabric.api.networking.handshake.v1;
