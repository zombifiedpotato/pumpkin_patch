package com.github.ZombifiedPatato.pumpkin_patch.origins.power.custom;

import com.github.ZombifiedPatato.pumpkin_patch.PumpkinPatch;
import com.github.ZombifiedPatato.pumpkin_patch.particle.ModParticles;
import com.github.ZombifiedPatato.pumpkin_patch.utility.Box;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.power.CooldownPower;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class BreathWeapon extends CooldownPower implements Active {

    private final List<StatusEffectInstance> effects = new LinkedList<>();
    private boolean setFire;
    private float damage;
    private boolean isCone;

    private ParticleEffect particle = ModParticles.RED_DRAGON_BREATH;

    public void setCone(boolean cone) {
        isCone = cone;
    }

    public void setColor(int colorInt) {
        switch (colorInt) {
            case 0 -> particle = ModParticles.RED_DRAGON_BREATH;
            case 1 -> particle = ModParticles.GREEN_DRAGON_BREATH;
            case 2 -> particle = ModParticles.BLUE_DRAGON_BREATH;
            case 3 -> particle = ModParticles.BLACK_DRAGON_BREATH;
        }
    }

    private Key key;

    public BreathWeapon(PowerType<?> type, LivingEntity entity, int cooldownDuration, HudRender hudRender) {
        super(type, entity, cooldownDuration, hudRender);
    }


    public void setSetFire(boolean setFire) {
        this.setFire = setFire;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public void addEffect(StatusEffectInstance effect) {
        effects.add(effect);
    }

    public static PowerFactory<Power> createFactory() {
        return new PowerFactory<>(new Identifier(PumpkinPatch.MOD_ID, "breath_weapon"),
                new SerializableData()
                        .add("cooldown", SerializableDataTypes.INT, 1)
                        .add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
                        .add("key", ApoliDataTypes.BACKWARDS_COMPATIBLE_KEY, new Active.Key())
                        .add("effect", SerializableDataTypes.STATUS_EFFECT_INSTANCE, null)
                        .add("effects", SerializableDataTypes.STATUS_EFFECT_INSTANCES, null)
                        .add("setFire", SerializableDataTypes.BOOLEAN, false)
                        .add("damage", SerializableDataTypes.FLOAT, 0f)
                        .add("isCone", SerializableDataTypes.BOOLEAN, false)
                        .add("colorInt", SerializableDataTypes.INT, 0),
                data ->
                        (type, player) -> {
                            BreathWeapon power = new BreathWeapon(type, player, data.getInt("cooldown"), data.get("hud_render"));
                            if (data.isPresent("effect")) {
                                power.addEffect(data.get("effect"));
                            }
                            if (data.isPresent("effects")) {
                                List<StatusEffectInstance> effects = data.get("effects");
                                for (StatusEffectInstance effect : effects) {
                                    power.addEffect(effect);
                                }
                            }
                            power.setSetFire(data.getBoolean("setFire"));
                            power.setDamage(data.getFloat("damage"));
                            power.setKey(data.get("key"));
                            power.setCone(data.getBoolean("isCone"));
                            power.setColor(data.getInt("colorInt"));
                            return power;
                        }
        ).allowCondition();
    }

    /**
     * Method to determine if breathweapon can be used
     */
    @Override
    public void onUse() {
        if(canUse()) {
            use();
        }
    }

    /**
     * Method which activates the breath weapon.
     */
    @Override
    public void use() {
        LivingEntity e = this.entity;
        List<LivingEntity> targets = getTargets();

        for (LivingEntity target : targets) {
            if (damage > 0) {
                target.damage(DamageSource.DRAGON_BREATH, damage);
            }
            if (setFire) {
                target.setOnFireFor(10);
            }
            for (StatusEffectInstance effect : effects) {
                target.setStatusEffect(new StatusEffectInstance(effect), e);
            }
        }
        super.use();
        System.out.println(effects);
    }

    /**
     * Method to get targets for breath weapon and calls particle spawner.
     * @return All entities which are effected by the breath weapon.
     */
    private List<LivingEntity> getTargets() {

        List<LivingEntity> entityTargets;
        Vec3d origin = this.entity.getEyePos();
        Vec3d forwardDirection = this.entity.getRotationVec(1).normalize();
        Vec3d upDirection = getRotationVec(this.entity.getPitch()-90, this.entity.getYaw()).normalize();
        Vec3d sideDirection = getRotationVec(0, this.entity.getYaw()+90).normalize();
        Box box;
        if (isCone) {
            // Calculate entity list for when shape is cone
            Vec3d lengthVector = forwardDirection.multiply(5);
            Vec3d heightVector = upDirection.multiply(2);
            Vec3d widthVector = sideDirection.multiply(4);
            Vec3d originPoint = origin.subtract(widthVector.multiply(0.5)).subtract(heightVector.multiply(0.5));
            box = new Box(originPoint, heightVector, widthVector, lengthVector);
            List<LivingEntity> entityTargetsTemp = box.getOtherLivingEntities(entity);
            entityTargets = new LinkedList<>();
            for (LivingEntity e : entityTargetsTemp) {
                if (this.entity.distanceTo(e) <= 5.2) {
                    entityTargets.add(e);
                }
            }
            spawnConeParticles(origin, forwardDirection, upDirection, sideDirection);
        } else {
            // Calculate entity list for when shape is line (not cone)
            Vec3d lengthVector = forwardDirection.multiply(15);
            Vec3d heightVector = upDirection.multiply(1);
            Vec3d widthVector = sideDirection.multiply(1);
            Vec3d originPoint = origin.subtract(widthVector.multiply(0.5)).subtract(heightVector.multiply(0.5));
            box = new Box(originPoint, heightVector, widthVector, lengthVector);
            entityTargets = box.getOtherLivingEntities(entity);
            spawnLineParticles(origin, lengthVector);
        }
        return entityTargets;
    }

    /**
     * Copy of Entity.getRotationVector to put in own pitch and yaw.
     * @param pitch to use for calculation
     * @param yaw to use for calculation
     * @return A 3d Vector which points in the direction calculated from pitch and yaw
     */
    private Vec3d getRotationVec(float pitch, float yaw) {
        float f = pitch * ((float)Math.PI / 180);
        float g = -yaw * ((float)Math.PI / 180);
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return new Vec3d(i * j, -k, h * j);
    }

    private void spawnConeParticles(Vec3d originPoint, Vec3d forward, Vec3d up, Vec3d side) {
        World world = this.entity.getWorld();
        Vec3d localTurnVector = localRotation(up, forward, -1.3963f).normalize().multiply(0.15f);
        side = localRotation(up, side, -1.3963f).normalize();
        localTurnVector = localRotation(side, localTurnVector, 0.872665f);
        Random random = new Random();
        for (int i = 0; i < 40; i++) {
            for (int j = 0; j < 10; j++) {
                world.addParticle(particle, originPoint.getX(), originPoint.getY(), originPoint.getZ(),
                        localTurnVector.getX() + random.nextDouble() * .01 -.005,
                        localTurnVector.getY() + random.nextDouble() * .01 -.005,
                        localTurnVector.getZ() + random.nextDouble() * .01 -.005);
                localTurnVector = localRotation(side, localTurnVector, -0.174533f);
            }
            localTurnVector = localRotation(side, localTurnVector, 1.7453f);
            localTurnVector = localRotation(up, localTurnVector, 0.069813f);
            side = localRotation(up, side, 0.069813f).normalize();
        }
    }

    private Vec3d localRotation (Vec3d direction, Vec3d vector, float angle) {
        return vector.multiply(Math.cos(angle))
                .add(direction.crossProduct(vector).multiply(Math.sin(angle)))
                .add(direction.multiply(direction.dotProduct(vector)).multiply(1-Math.cos(angle)));
    }

    private void spawnLineParticles(Vec3d originPoint, Vec3d forward) {
        World world = this.entity.getWorld();
        Vec3d calculatedForward = forward.normalize().multiply(0.4);
        Random random = new Random();
        for (int i = 0; i < 50; i++) {
            world.addParticle(particle, originPoint.getX(), originPoint.getY(), originPoint.getZ(),
                    calculatedForward.getX() + random.nextDouble() * .2 -.1,
                    calculatedForward.getY() + random.nextDouble() * .2 -.1,
                    calculatedForward.getZ() + random.nextDouble() * .2 -.1);
        }
    }

    @Override
    public Key getKey() {
        return key;
    }

    @Override
    public void setKey(Key key) {
        this.key = key;
    }
}
