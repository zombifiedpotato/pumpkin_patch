package com.github.ZombifiedPatato.pumpkin_patch.entity.custom;

import com.github.ZombifiedPatato.pumpkin_patch.PumpkinPatchClient;
import com.github.ZombifiedPatato.pumpkin_patch.entity.ModEntities;
import com.github.ZombifiedPatato.pumpkin_patch.item.ModItems;
import com.github.ZombifiedPatato.pumpkin_patch.networking.EntitySpawnPacket;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import com.github.ZombifiedPatato.pumpkin_patch.particle.ModParticles;

import java.util.List;

public class FairyPowderEntity extends ThrownItemEntity {

    public FairyPowderEntity(EntityType<? extends FairyPowderEntity> entityType, World world) {
        super(entityType, world);
        this.ignoreCameraFrustum = true;
    }

    public FairyPowderEntity(World world, LivingEntity owner) {
        super(ModEntities.FAIRY_POWDER, owner, world);
        this.ignoreCameraFrustum = true;
    }

    public FairyPowderEntity(World world, double x, double y, double z) {
        super(ModEntities.FAIRY_POWDER, x, y, z, world);
        this.ignoreCameraFrustum = true;
    }

    @Override
    public Item getDefaultItem() {
        return ModItems.FAIRY_POWDER;
    }

    @Override
    protected float getGravity() {
        return 0.04f;
    }

    @Override
    public void handleStatus(byte status) {
        if (status == 3) {
            float radius = 1.0f;
            ParticleEffect particleEffect = ModParticles.PINK_SMOKE;
            for (int j = 0; j < 16; ++j) {
                double velocityZ;
                double velocityY;
                double velocityX;
                float h = this.random.nextFloat() * ((float)Math.PI * 2);
                float k = MathHelper.sqrt(this.random.nextFloat()) * radius;
                double posX = this.getX() + (double)(MathHelper.cos(h) * k);
                double posY = this.getY();
                double posZ = this.getZ() + (double)(MathHelper.sin(h) * k);
                velocityX = (0.5 - this.random.nextDouble()) * 0.15;
                velocityY = 0.01f;
                velocityZ = (0.5 - this.random.nextDouble()) * 0.15;
                this.world.addImportantParticle(particleEffect, posX, posY, posZ, velocityX, velocityY, velocityZ);
            }
        }
    }


    protected void onCollision(HitResult hitResult){
        super.onCollision(hitResult);
        if (this.world.isClient) {
            return;
        }
        effectEntities();
        this.world.sendEntityStatus(this,(byte)3);
        this.kill();
    }

    private void effectEntities() {
        Box box = this.getBoundingBox().expand(4.0, 2.0, 4.0);
        List<LivingEntity> list = this.world.getNonSpectatingEntities(LivingEntity.class, box);
        if (!list.isEmpty()) {
            for (LivingEntity livingEntity : list) {
                double distance;
                if (!((distance = this.squaredDistanceTo(livingEntity)) < 16.0)) continue;
                double timeMultiplier = 1.0 - Math.sqrt(distance) / 4;
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING,
                        (int)(timeMultiplier * 200 + 200)));
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION,
                        (int)(timeMultiplier * 200 + 100)));
                }
            }
        }

    @Override
    public Packet<?> createSpawnPacket() {
        System.out.println("Creating spawn packet for fairy powder!");
        return EntitySpawnPacket.create(this, PumpkinPatchClient.SPAWN_PACKET_ID);
    }
}