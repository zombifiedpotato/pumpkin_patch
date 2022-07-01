package com.github.ZombifiedPatato.pumpkin_patch.particle.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.MathHelper;

public class DragonBreathParticle extends SpriteBillboardParticle {


    private boolean reachedGround;
    private final SpriteProvider spriteProvider;

    DragonBreathParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider, float red, float green, float blue) {
        super(world, x, y, z);
        this.velocityMultiplier = 0.96f;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
        this.red = MathHelper.nextFloat(this.random, Math.max(0.0f, red-0.05f), Math.min(1.0f, red+0.05f));
        this.green = MathHelper.nextFloat(this.random, Math.max(0.0f, green-0.05f), Math.min(1.0f, green+0.05f));
        this.blue = MathHelper.nextFloat(this.random, Math.max(0.0f, blue-0.05f), Math.min(1.0f, blue+0.05f));
        this.scale *= 0.75f;
        this.maxAge = (int)(20.0 / ((double)this.random.nextFloat() * 0.8 + 0.2));
        this.reachedGround = false;
        this.collidesWithWorld = false;
        this.spriteProvider = spriteProvider;
        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }
        this.setSpriteForAge(this.spriteProvider);
        if (this.onGround) {
            this.velocityY = 0.0;
            this.reachedGround = true;
        }
        if (this.reachedGround) {
            this.velocityY += 0.002;
        }
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        if (this.y == this.prevPosY) {
            this.velocityX *= 1.1;
            this.velocityZ *= 1.1;
        }
        this.velocityX *= this.velocityMultiplier;
        this.velocityZ *= this.velocityMultiplier;
        if (!this.reachedGround) {
            this.velocityY *= this.velocityMultiplier;
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public float getSize(float tickDelta) {
        return this.scale * MathHelper.clamp(((float)this.age + tickDelta) / (float)this.maxAge * 32.0f, 0.0f, 1.0f);
    }

    @Environment(value= EnvType.CLIENT)
    public static class Factory
            implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new DragonBreathParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider, 0.2f, 0.0f, 0.8f);
        }
    }
}
