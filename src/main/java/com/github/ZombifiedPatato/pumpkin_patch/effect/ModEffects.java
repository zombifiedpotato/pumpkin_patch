package com.github.ZombifiedPatato.pumpkin_patch.effect;

import com.github.ZombifiedPatato.pumpkin_patch.PumpkinPatch;
import com.github.ZombifiedPatato.pumpkin_patch.effect.custom.LightningEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.awt.*;

public class ModEffects {
    public static final StatusEffect LIGHTNING = Registry.register(Registry.STATUS_EFFECT, new Identifier(PumpkinPatch.MOD_ID, "lightning"),
            new LightningEffect(StatusEffectCategory.HARMFUL, 3335601));

    public static void registerEffects() {
        System.out.println("Registering StatusEffects for " + PumpkinPatch.MOD_ID);
    }
}
