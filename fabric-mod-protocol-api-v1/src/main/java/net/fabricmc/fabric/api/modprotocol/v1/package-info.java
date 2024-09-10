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
 * The Mod Protocol API, version 1.
 *
 * <p>Mod Protocol is an additional syncing system allowing mods to define their protocol, with simple utilities
 * allowing to check supported version and use it for networking or safeguards against using mismatched/incompatible
 * mod versions on client and server. Exact configuration can differ from mod to mod.</p>
 *
 * <p>The mod protocol can be defined in two ways:
 * <dl>
 *     <dt>fabric.mod.json</dt>
 *     <dd>This is the simplest way to define it. Can be useful when you don't need to change it depending on mods configuration
 *     or external dependencies. It is set within custom field of that file under "fabric:mod_protocol" key.
 *
 *     It can be defined in multiple ways:
 *     <dl>
 *     		<dt>"fabric:mod_protocol": 1</dt>
 *     		<dd><p>This will automatically use mods id with "mod" namespace as the protocol identifier, as a single supported protocol version,
 *     		with display name and version being copied form mods metadata. It also marks the protocol as required on both client and server.</p>
 *     		</dd>
 *     		<dt>"fabric:mod_protocol": {
 *     		 "protocol": [1, 2],
 *     		 "id: "custom:id",
 *     		 "name": "Mod Name",
 *     		 "version": "v1.2.3",
 *     		 "require_client": false,
 *     		 "require_server": true
 *     		}</dt>
 *      	<dd><p>Full object. Only required value is "protocol", which can be set directly for single version or as an array for multiple.</p>
 *      	<p>"id" is the protocols identifier, which can have any namespace and path, as long as it's valid.
 *      	It is optional and defaults to an ID with "mod" namespace and path equal to mod's id.
 *      	</p>
 *     		<p>"name" is a name displayed if protocol doesn't match. It's optional and by default it uses one from mod's metadata.</p>
 *          <p>"version" is a version displayed if protocol doesn't match. It's optional and by default it uses one from mod's metadata.</p>
 *          <p>"require_client" controls if clients without this protocol can join the server, defaults to true, preventing joining</p>
 *          <p>"require_server" controls if clients can join servers without this mod, defaults to true, preventing joining</p>
 *       	</dd>
 *       	<dt>"fabric:mod_protocol": [{
 *       		 "protocol": [1, 2],
 *       		 "id: "custom:id",
 *       		 "name": "Mod Name",
 *       		 "version": "v1.2.3",
 *       		 "require_client": false,
 *       		 "require_server": true
 *       		}]</dt>
 *        	<dd><p>Array of full objects. Allows to define multiple versions of the protocol. The inner objects use the same format as single-full object format,
 *        	with main exception being that fields "id", "name" and "version" aren't defaulted and need to be always set</p></dd>
 *     </dl>
 *     </dd>
 *     <dt>{@link net.fabricmc.fabric.api.modprotocol.v1.ModProtocolRegistry}</dt>
 *     <dd>This is the simplest way to define it. Can be useful when you don't need to change it depending on mods configuration
 *     or external dependencies. It is set within custom field of that file under "fabric:mod_protocol" key.
 *     </dd>
 * </dl>
 * </p>
 */
@ApiStatus.Experimental
package net.fabricmc.fabric.api.modprotocol.v1;

import org.jetbrains.annotations.ApiStatus;
