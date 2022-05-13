package com.github.ZombifiedPatato.pumpkin_patch.effect.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class LightningEffect extends StatusEffect {

    public LightningEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {

            Random random = new Random();
            if (Math.round(random.nextDouble()*30) >= 30-amplifier * 2L) {
                    entity.setVelocity(random.nextDouble() * 1.5 - (1.5 * 0.5), entity.getVelocity().getY(), random.nextDouble() * 1.5 - (1.5 * 0.5));
                    entity.damage(DamageSource.LIGHTNING_BOLT, 1f);
                    entity.playSound(SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, 2.5f, 1f);
            }

        super.applyUpdateEffect(entity, amplifier);
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        Random random = new Random();
        entity.playSound(SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, 4f, 1f);
        entity.setVelocity(random.nextDouble() * 2 - (2 * 0.5), entity.getVelocity().getY() + 1, random.nextDouble() * 2- (2 * 0.5));
        entity.damage(DamageSource.LIGHTNING_BOLT, 4f);
        super.onApplied(entity, attributes, amplifier);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}
