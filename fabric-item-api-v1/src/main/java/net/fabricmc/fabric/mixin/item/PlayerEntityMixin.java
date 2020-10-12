package net.fabricmc.fabric.mixin.item;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.fabric.impl.item.ItemExtensions;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Unique
    private boolean muteSound = false;

    /**
     * Captures all of the {@code World#playSound(PlayerEntity, double, double, double, net.minecraft.sound.SoundEvent, net.minecraft.sound.SoundCategory, float, float)}<p>
     * and replaces the volume argument with 0 to mute it if muteSound has been set
     * @param volume
     * @return
     */
    @ModifyArg(
        at = @At(
            value="INVOKE",
            target="net/minecraft/world/World.playSound(Lnet/minecraft/entity/player/PlayerEntity;DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"
            ),
        index = 6,
        method = "attack")
    public float muteSoundEvent(float volume)
    {
        if(muteSound) 
        {
            muteSound = false;
            return 0.0F;
        }
        return volume;
    }

    @Inject(
    at = @At(
        value="FIELD",
        target="net/minecraft/sound/SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK:Lnet/minecraft/sound/SoundEvent;",
        opcode = Opcodes.GETSTATIC),
    method = "attack",
    cancellable = false)
    public void onPlayKnockbackSound(CallbackInfo ci)
    {
        PlayerEntity pe = (PlayerEntity)(Object)this;
        ItemExtensions item = ((ItemExtensions)pe.getMainHandStack().getItem());
        if(item.fabric_getKnockBackHitSound() != null)
        {
            this.muteSound = true;
            pe.world.playSound(null, pe.getX(), pe.getY(), pe.getZ(), item.fabric_getKnockBackHitSound(), pe.getSoundCategory(), 1.0F, 1.0F);
        }
    }
    @Inject(
        at = @At(
            value="FIELD",
            target="net/minecraft/sound/SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP:Lnet/minecraft/sound/SoundEvent;",
            opcode = Opcodes.GETSTATIC),
        method = "attack",
        cancellable = false)
    public void onPlaySweepSound(CallbackInfo ci)
    {
        PlayerEntity pe = (PlayerEntity)(Object)this;
        ItemExtensions item = ((ItemExtensions)pe.getMainHandStack().getItem());
        if(item.fabric_getSweepingHitSound() != null)
        {
            this.muteSound = true;
            pe.world.playSound(null, pe.getX(), pe.getY(), pe.getZ(), item.fabric_getSweepingHitSound(), pe.getSoundCategory(), 1.0F, 1.0F);
        }
    }
    @Inject(
        at = @At(
            value="FIELD",
            target="net/minecraft/sound/SoundEvents.ENTITY_PLAYER_ATTACK_CRIT:Lnet/minecraft/sound/SoundEvent;",
            opcode = Opcodes.GETSTATIC),
        method = "attack",
        cancellable = false)
    public void onPlayCritSound(CallbackInfo ci)
    {
        PlayerEntity pe = (PlayerEntity)(Object)this;
        ItemExtensions item = ((ItemExtensions)pe.getMainHandStack().getItem());
        if(item.fabric_getCriticalHitSound() != null)
        {
            this.muteSound = true;
            pe.world.playSound(null, pe.getX(), pe.getY(), pe.getZ(), item.fabric_getCriticalHitSound(), pe.getSoundCategory(), 1.0F, 1.0F);
        }
    }
    @Inject(
        at = @At(
            value="FIELD",
            target="net/minecraft/sound/SoundEvents.ENTITY_PLAYER_ATTACK_STRONG:Lnet/minecraft/sound/SoundEvent;",
            opcode = Opcodes.GETSTATIC),
        method = "attack",
        cancellable = false)
    public void onPlayStrongSound(CallbackInfo ci)
    {
        PlayerEntity pe = (PlayerEntity)(Object)this;
        ItemExtensions item = ((ItemExtensions)pe.getMainHandStack().getItem());
        if(item.fabric_getStrongHitSound() != null)
        {
            this.muteSound = true;
            pe.world.playSound(null, pe.getX(), pe.getY(), pe.getZ(), item.fabric_getStrongHitSound(), pe.getSoundCategory(), 1.0F, 1.0F);
        }
    }
    @Inject(
        at = @At(
            value="FIELD",
            target="net/minecraft/sound/SoundEvents.ENTITY_PLAYER_ATTACK_WEAK:Lnet/minecraft/sound/SoundEvent;",
            opcode = Opcodes.GETSTATIC),
        method = "attack",
        cancellable = false)
    public void onPlayWeakSound(CallbackInfo ci)
    {
        PlayerEntity pe = (PlayerEntity)(Object)this;
        ItemExtensions item = ((ItemExtensions)pe.getMainHandStack().getItem());
        if(item.fabric_getWeakHitSound() != null)
        {
            this.muteSound = true;
            pe.world.playSound(null, pe.getX(), pe.getY(), pe.getZ(), item.fabric_getWeakHitSound(), pe.getSoundCategory(), 1.0F, 1.0F);
        }
    }
    @Inject(
        at = @At(
            value="FIELD",
            target="net/minecraft/sound/SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE:Lnet/minecraft/sound/SoundEvent;",
            opcode = Opcodes.GETSTATIC),
        method = "attack",
        cancellable = false)
    public void onPlayNoDamageSound(CallbackInfo ci)
    {
        PlayerEntity pe = (PlayerEntity)(Object)this;
        ItemExtensions item = ((ItemExtensions)pe.getMainHandStack().getItem());
        if(item.fabric_getNoDamageHitSound() != null)
        {
            this.muteSound = true;
            pe.world.playSound(null, pe.getX(), pe.getY(), pe.getZ(), item.fabric_getNoDamageHitSound(), pe.getSoundCategory(), 1.0F, 1.0F);
        }
    }
    
    
}
