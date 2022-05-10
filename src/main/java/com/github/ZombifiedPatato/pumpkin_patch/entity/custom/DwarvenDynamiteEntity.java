package com.github.ZombifiedPatato.pumpkin_patch.entity.custom;

import com.github.ZombifiedPatato.pumpkin_patch.entity.ModEntities;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

public class DwarvenDynamiteEntity extends Entity {


    private static final TrackedData<Integer> FUSE = DataTracker.registerData(TntEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final int DEFAULT_FUSE = 140;
    @Nullable
    private LivingEntity causingEntity;

    public DwarvenDynamiteEntity(EntityType<? extends DwarvenDynamiteEntity> entityType, World world) {
        super(entityType, world);
        this.intersectionChecked = true;
    }

    public DwarvenDynamiteEntity(World world, double x, double y, double z, @Nullable LivingEntity igniter) {
        this((EntityType<? extends DwarvenDynamiteEntity>) ModEntities.DWARVEN_DYNAMITE, world);
        this.setPosition(x, y, z);
        double d = world.random.nextDouble() * 6.2831854820251465;
        this.setVelocity(-Math.sin(d) * 0.02, 0.2f, -Math.cos(d) * 0.02);
        this.setFuse(140);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
        this.causingEntity = igniter;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(FUSE, 140);
    }

    @Override
    protected MoveEffect getMoveEffect() {
        return MoveEffect.NONE;
    }

    @Override
    public boolean collides() {
        return !this.isRemoved();
    }

    @Override
    public void tick() {
        if (!this.hasNoGravity()) {
            this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
        }

        this.move(MovementType.SELF, this.getVelocity());
        this.setVelocity(this.getVelocity().multiply(0.98));
        if (this.onGround) {
            this.setVelocity(this.getVelocity().multiply(0.7, -0.5, 0.7));
        }

        int i = this.getFuse() - 1;
        this.setFuse(i);
        if (i <= 0) {
            this.discard();
            if (!this.world.isClient) {
                this.explode();
            }
        } else {
            this.updateWaterState();
            if (this.world.isClient) {
                this.world.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }

    private void explode() {
        this.world.createExplosion(this, this.getX(), this.getBodyY(0.0625), this.getZ(), 7.0F, Explosion.DestructionType.BREAK);
        int radius = 3 + 1;
        for (int x = this.getBlockX() - radius; x <= this.getBlockX() + radius; x++) {
            for(int y = this.getBlockY() - radius; y <= this.getBlockY() + radius; y++) {
                for (int z = this.getBlockZ() - radius; z <= this.getBlockZ() + radius; z++) {
                    if (this.getBlockPos().isWithinDistance(new BlockPos(x,y,z), radius - 1) &&
                            this.getWorld().getBlockState(new BlockPos(x,y,z)).getBlock().equals(Blocks.OBSIDIAN)) {
                        this.getWorld().breakBlock(new BlockPos(x,y,z), true);
                    }
                }
            }
        }
        this.world.createExplosion(this, this.getX(), this.getBodyY(0.0625), this.getZ(), 3.0F, Explosion.DestructionType.BREAK);

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putShort("Fuse", (short)this.getFuse());
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.setFuse(nbt.getShort("Fuse"));
    }

    @Nullable
    public LivingEntity getCausingEntity() {
        return this.causingEntity;
    }

    @Override
    protected float getEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 0.15f;
    }

    public void setFuse(int fuse) {
        this.dataTracker.set(FUSE, fuse);
    }

    public int getFuse() {
        return this.dataTracker.get(FUSE);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}
