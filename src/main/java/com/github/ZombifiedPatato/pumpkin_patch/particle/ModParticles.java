package com.github.ZombifiedPatato.pumpkin_patch.particle;

import com.github.ZombifiedPatato.pumpkin_patch.PumpkinPatch;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;


public class ModParticles {

    public static final DefaultParticleType PINK_SMOKE = Registry.register(
            Registry.PARTICLE_TYPE, new Identifier(PumpkinPatch.MOD_ID, "pink_smoke"), FabricParticleTypes.simple(true));

    public static final DefaultParticleType RED_DRAGON_BREATH = Registry.register(
            Registry.PARTICLE_TYPE, new Identifier(PumpkinPatch.MOD_ID, "red_dragon_breath"), FabricParticleTypes.simple(true));
    public static final DefaultParticleType GREEN_DRAGON_BREATH = Registry.register(
            Registry.PARTICLE_TYPE, new Identifier(PumpkinPatch.MOD_ID, "green_dragon_breath"), FabricParticleTypes.simple(true));
    public static final DefaultParticleType BLUE_DRAGON_BREATH = Registry.register(
            Registry.PARTICLE_TYPE, new Identifier(PumpkinPatch.MOD_ID, "blue_dragon_breath"), FabricParticleTypes.simple(true));
    public static final DefaultParticleType BLACK_DRAGON_BREATH = Registry.register(
            Registry.PARTICLE_TYPE, new Identifier(PumpkinPatch.MOD_ID, "black_dragon_breath"), FabricParticleTypes.simple(true));


    public static void registerModParticles() {
        System.out.println("Registering Mod Particles for " + PumpkinPatch.MOD_ID);
    }
}
