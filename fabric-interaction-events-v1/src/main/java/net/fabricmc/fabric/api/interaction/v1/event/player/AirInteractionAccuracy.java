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

package net.fabricmc.fabric.api.interaction.v1.event.player;

/**
 * Represents the accuracy of dispatched events relating to using and attacking air.
 *
 * <p>A client with fabric-interaction-events-v1 installed will always have a {@link AirInteractionAccuracy#DEFINITE} value.
 * If Fabric API is not available on the client then the server will calculate whether it is likely that the client has used or attacked air.
 */
public enum AirInteractionAccuracy {
	/**
	 * Represents an accuracy that says it is likely the client has attacked or used air.
	 */
	LIKELY,
	/**
	 * Represents an accuracy that says it is 100% definite the client has attacked or used air.
	 */
	DEFINITE;
}
