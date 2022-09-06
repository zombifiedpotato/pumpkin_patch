package com.github.zombifiedpotato.pumpkin_patch.particle;

import com.github.zombifiedpotato.pumpkin_patch.PumpkinPatch;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;


public class ModParticles {

    public static final DefaultParticleType PINK_SMOKE = Registry.register(
            Registry.PARTICLE_TYPE, new Identifier(PumpkinPatch.MOD_ID, "pink_smoke"), FabricParticleTypes.simple(true));

    public static void registerModParticles() {
        System.out.println("Registering Mod Particles for " + PumpkinPatch.MOD_ID);
    }
}
