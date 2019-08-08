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

import java.util.Arrays;
import java.util.Iterator;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class FailureReason {
    
    public FailureReason(String modid2, FailureType type2, String reason2) {
        modid = modid2;
        type = type2;
        reason = reason2;
    }

    public static FailureReason create(String modid, FailureType type, String reason) {
        return new FailureReason(modid, type, reason);
    }
    
    public static Text buildFailureMessage(String modid, FailureReason... reasons) {
        TranslatableText mainText = new TranslatableText("fabric-networking-v0.failed.base", modid);
        
        Iterator<FailureReason> iterator = Arrays.asList(reasons).iterator();
        
        while(iterator.hasNext()) {
            
            FailureReason reason = iterator.next();
            
            switch(reason.getType()) {
            case WRONG_VERSION:
                mainText.append(new TranslatableText("fabric-networking-v0.failed.section.wrong_version", reason.getReason()));
                break;
            default:
                break;
            }
            
            if(iterator.hasNext()) {
                
            } else {
                
            }
            
        }
        
        
        
        return mainText;
    }
    
    private String modid;
    private FailureType type;
    private String reason;
    
    public String getModId() {
        return modid;
    }
    
    public FailureType getType() {
        return type;
    }
    
    public String getReason() {
        return reason;
    }
    
    

    public enum FailureType {
        WRONG_VERSION;
    }
    
    
}
