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

package net.fabricmc.fabric.impl.client.modelevents;

import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.fabric.api.client.modelevents.v1.data.PartView;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPart.Cuboid;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

@ApiStatus.Internal
public final class FabricPartHooks {
    private static final Logger LOGGER = LoggerFactory.getLogger("fabric-model-events-v1");

    private final Container part;
    @Nullable
    private FabricPartHooks parent;
    @Nullable
    private PartViewImpl view;

    private boolean settingPath;

    public FabricPartHooks(Container part) {
        this.part = part;
    }

    public void setParent(FabricPartHooks parent) {
        this.parent = parent;
    }

    // Note: We only create our data structures on the first usage, not construction.
    //       This is to ensure consistent behavior is maintained even when mods make
    //       changes to the part's contents late after construction.
    public void onPartRendered(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        try {
            if (view == null) {
                getRoot().setPath(PartTreePathImpl.EMPTY);
                if (view == null) {
                    // this shouldn't happen, but just in case it does assume we are the root (or I guess we're an orphan).
                    setPath(PartTreePathImpl.EMPTY);
                }
            }
        } finally {
            // cleanup
            parent = null;
        }

        view.dispatchEvents(matrices, vertices, light, overlay, red, green, blue, alpha);
    }

    // called recursively
    void setPath(PartTreePathImpl path) {
        if (settingPath) {
            LOGGER.warn("Loop detected in model tree at $", path);
            return; // Parts shouldn't have loops, but just in case, prevent re-entry
        }
        settingPath = true;
        try {
            this.view = new PartViewImpl(part, path);
            // cleanup
            parent = null;
            part.getChildren().forEach((name, child) -> {
                Container.of(child).getHooks().setPath(path.append(name));
            });
        } finally {
            settingPath = false;
        }
    }

    @Nullable
    public PartView getView() {
        return view;
    }

    private FabricPartHooks getRoot() {
        return parent == null ? this : parent.getRoot();
    }

    @ApiStatus.Internal
    public interface Container {
        FabricPartHooks getHooks();

        default ModelPart asPart() {
            return (ModelPart)this;
        }

        List<Cuboid> getCuboids();

        Map<String, ModelPart> getChildren();

        static Container of(ModelPart part) {
            return (Container)part;
        }
    }
}
