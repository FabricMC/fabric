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

package net.fabricmc.fabric.api.fluid;

import java.util.Collection;
import java.util.Optional;

import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;

/**
 * Fabric fluid type tags are distinct from {@link FluidTags#} in that Fabric tags
 * may include fluids that lack an in-world representation. 
 * 
 * The primary use of these tags is to declare and test fluid equivalence 
 * for crafting and processing recipes.
 */
public class FabricFluidTypeTags {
    private static TagContainer<FabricFluidType> container = new TagContainer<>((id) -> {
        return Optional.empty();
     }, "", false, "");
    
     private static int containerChanges;
     
     public static final Tag<FabricFluidType> WATER = register("water");
     public static final Tag<FabricFluidType> LAVA = register("lava");

     public static void setContainer(TagContainer<FabricFluidType> tagContainer_1) {
        container = tagContainer_1;
        ++containerChanges;
     }

     private static Tag<FabricFluidType> register(String idString) {
        return new FabricFluidTypeTag(new Identifier(idString));
     }

     public static class FabricFluidTypeTag extends Tag<FabricFluidType> {
        private int lastChange = -1;
        private Tag<FabricFluidType> tag;

        public FabricFluidTypeTag(Identifier id) {
           super(id);
        }

        @Override
        public Collection<FabricFluidType> values() {
           if (this.lastChange != containerChanges) {
              this.tag = container.getOrCreate(this.getId());
              this.lastChange = containerChanges;
           }
           return this.tag.values();
        }

        @Override
        public Collection<Tag.Entry<FabricFluidType>> entries() {
           if (this.lastChange != containerChanges) {
              this.tag = container.getOrCreate(this.getId());
              this.lastChange = containerChanges;
           }
           return this.tag.entries();
        }
     }
}
