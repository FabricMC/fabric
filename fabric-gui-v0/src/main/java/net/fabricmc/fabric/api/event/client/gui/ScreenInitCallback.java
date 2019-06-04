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
package net.fabricmc.fabric.api.event.client.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;

/**
 * This event is emitted on the client to initialize and redraw gui screens
 * whenever opened. Mods consuming this event are allowed to interact with
 * the screen to add their own buttons and elements.
 *
 * Additional credit to Killjoy1121 for the initial implementation of this event.
 */
@FunctionalInterface
public interface ScreenInitCallback {
    /**
     * Event bus for mods to subscribe to this event.
     *
     * Usage:
     *   ScreenInitCallback.EVENT.subscribe((screen, buttons) -> {...});
     */
    Event<ScreenInitCallback> EVENT = EventFactory.createArrayBacked(ScreenInitCallback.class, listeners -> (screen, buttons) -> {
        for (ScreenInitCallback event : listeners) {
            event.init(screen, buttons);
        }
    });

    /**
     * Callback for when a screen is initialized.
     *
     * @param screen the current screen being displayed
     * @param buttons a writeable view of the screen's button list.
     */
    void init(Screen screen, ButtonList buttons);

    /**
     * The list of buttons currently being added to the screen.
     */
    interface ButtonList {
        /**
         * Adds a new button to the screen's own button list.
         * This is the same as calling `adButton(button)` on the screen
         * itself and likewise add the button to the screen's elements list.
         */
        <T extends AbstractButtonWidget> T add(T button);
    }
}
