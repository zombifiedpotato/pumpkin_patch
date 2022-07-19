package com.github.ZombifiedPatato.pumpkin_patch.origins.power.custom;

import com.github.ZombifiedPatato.pumpkin_patch.PumpkinPatch;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.power.CooldownPower;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;

public class CircleAttack extends CooldownPower implements Active {

    private Key key;
    private final List<StatusEffectInstance> targetEffects = new LinkedList<>();
    private final List<StatusEffectInstance> userEffects = new LinkedList<>();
    private boolean setFire;
    private float damage;
    private boolean throwAway;
    private float throwPower;
    private float circleRadius;


    public void setSetFire(boolean setFire) {
        this.setFire = setFire;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public void setThrowAway(boolean throwAway) {
        this.throwAway = throwAway;
    }

    public void setThrowPower(float throwPower) {
        this.throwPower = throwPower;
    }
    public void setCircleRadius(float circleRadius) {
        this.circleRadius = circleRadius;
    }

    public void addTargetEffect(StatusEffectInstance effect) {
        targetEffects.add(effect);
    }

    public void addUserEffect(StatusEffectInstance effect) {
        userEffects.add(effect);
    }

    public CircleAttack(PowerType<?> type, LivingEntity entity, int cooldownDuration, HudRender hudRender) {
        super(type, entity, cooldownDuration, hudRender);
    }

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
        List<LivingEntity> targets = getTargets();
        for (LivingEntity target : targets) {
            target.damage(new EntityDamageSource("magic", this.entity), damage);
            if (setFire) {
                target.setOnFireFor(10);
            }
            if (throwAway) {
                throwTarget(target);
            }
            for (StatusEffectInstance effect : targetEffects) {
                target.setStatusEffect(new StatusEffectInstance(effect), this.entity);
            }
        }
        for (StatusEffectInstance effect : userEffects) {
            this.entity.setStatusEffect(new StatusEffectInstance(effect), this.entity);
        }
        spawnParticles();
        super.use();
    }

    private void throwTarget(LivingEntity target) {
        Vec3d pos = this.entity.getPos();
        Vec3d targetPos = target.getEyePos();
        Vec3d throwVector = targetPos.subtract(pos).multiply(throwPower);
        target.setVelocity(throwVector);
    }

    private List<LivingEntity> getTargets() {
        Vec3d origin = this.entity.getPos();
        List<LivingEntity> targets = new LinkedList<>();
        Box box = new Box(origin.add(circleRadius, circleRadius, circleRadius), origin.add(-circleRadius,-circleRadius,-circleRadius));
        List<Entity> list = this.entity.getEntityWorld().getOtherEntities(this.entity, box, entity1 -> this.entity.distanceTo(entity1) <= 4);
        for (Entity e : list) {
            if (e instanceof LivingEntity living) {
                targets.add(living);
            }
        }
        return targets;
    }

    private void spawnParticles() {
        Vec3d pos = this.entity.getPos();
        Vec3d speed = new Vec3d(circleRadius * .05, .1, 0);
        World world = this.entity.getWorld();
        for (int i = 0; i < 36; i++){
            world.addParticle(ParticleTypes.FLAME, true, pos.getX(), pos.getY(), pos.getZ(),
                    speed.getX(), speed.getY(), speed.getZ());
            speed = localRotation(new Vec3d(0,1,0), speed, 0.174533f);
        }
    }

    private Vec3d localRotation (Vec3d direction, Vec3d vector, float angle) {
        return vector.multiply(Math.cos(angle))
                .add(direction.crossProduct(vector).multiply(Math.sin(angle)))
                .add(direction.multiply(direction.dotProduct(vector)).multiply(1-Math.cos(angle)));
    }

    @Override
    public Key getKey() {
        return key;
    }

    @Override
    public void setKey(Key key) {
        this.key = key;
    }

    public static PowerFactory<Power> createFactory() {
        return new PowerFactory<>(new Identifier(PumpkinPatch.MOD_ID, "circle_attack"),
                new SerializableData()
                        .add("cooldown", SerializableDataTypes.INT, 1)
                        .add("hud_render", ApoliDataTypes.HUD_RENDER, HudRender.DONT_RENDER)
                        .add("key", ApoliDataTypes.BACKWARDS_COMPATIBLE_KEY, new Active.Key())
                        .add("target_effect", SerializableDataTypes.STATUS_EFFECT_INSTANCE, null)
                        .add("target_effects", SerializableDataTypes.STATUS_EFFECT_INSTANCES, null)
                        .add("setFire", SerializableDataTypes.BOOLEAN, false)
                        .add("damage", SerializableDataTypes.FLOAT, 0f)
                        .add("throwAway", SerializableDataTypes.BOOLEAN, false)
                        .add("user_effect", SerializableDataTypes.STATUS_EFFECT_INSTANCE, null)
                        .add("user_effects", SerializableDataTypes.STATUS_EFFECT_INSTANCES, null)
                        .add("throwPower", SerializableDataTypes.FLOAT, .3f)
                        .add("circle_radius", SerializableDataTypes.FLOAT, 4f),
                data ->
                        (type, player) -> {
                            CircleAttack power = new CircleAttack(type, player, data.getInt("cooldown"), data.get("hud_render"));
                            if (data.isPresent("target_effect")) {
                                power.addTargetEffect(data.get("target_effect"));
                            }
                            if (data.isPresent("target_effects")) {
                                List<StatusEffectInstance> effects = data.get("target_effects");
                                for (StatusEffectInstance effect : effects) {
                                    power.addTargetEffect(effect);
                                }
                            }
                            if (data.isPresent("user_effect")) {
                                power.addUserEffect(data.get("user_effect"));
                            }
                            if (data.isPresent("user_effects")) {
                                List<StatusEffectInstance> effects = data.get("user_effects");
                                for (StatusEffectInstance effect : effects) {
                                    power.addUserEffect(effect);
                                }
                            }
                            power.setSetFire(data.getBoolean("setFire"));
                            power.setDamage(data.getFloat("damage"));
                            power.setKey(data.get("key"));
                            power.setThrowAway(data.getBoolean("throwAway"));
                            power.setThrowPower(data.getFloat("throwPower"));
                            power.setCircleRadius(data.getFloat("circle_radius"));
                            return power;
                        }
        ).allowCondition();
    }
}
