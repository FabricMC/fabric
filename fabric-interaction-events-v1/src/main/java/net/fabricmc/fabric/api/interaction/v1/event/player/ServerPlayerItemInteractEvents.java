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

import net.fabricmc.fabric.api.event.Event;

public final class ServerPlayerItemInteractEvents {
	public static final Event<Allow> ALLOW = ;

	public static final Event<InterceptDefaultAction> INTERCEPT_DEFAULT_ACTION = ;

	public static final Event<Before> BEFORE = ;

	public static final Event<After> AFTER = ;

	private ServerPlayerItemInteractEvents() {
	}

	@FunctionalInterface
	public interface Allow {
	}

	@FunctionalInterface
	public interface InterceptDefaultAction {
	}

	@FunctionalInterface
	public interface Before {
	}

	@FunctionalInterface
	public interface After {
	}
}
