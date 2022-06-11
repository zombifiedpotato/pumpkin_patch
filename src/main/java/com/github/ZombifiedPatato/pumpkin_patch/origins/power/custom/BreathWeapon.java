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
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.LinkedList;
import java.util.List;

public class BreathWeapon extends CooldownPower implements Active {

    private final List<StatusEffectInstance> effects = new LinkedList<>();
    private boolean setFire;
    private float damage;
    private boolean isCone;

    public void setCone(boolean cone) {
        isCone = cone;
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
                        .add("isCone", SerializableDataTypes.BOOLEAN, false),
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
                target.setStatusEffect(effect, e);
            }
        }
        super.use();
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
            Vec3d originPoint = origin.subtract(widthVector).subtract(heightVector.multiply(0.5));
            box = new Box(originPoint, heightVector, widthVector, lengthVector);
            List<LivingEntity> entityTargetsTemp = box.getOtherLivingEntities(entity);
            entityTargets = new LinkedList<>();
            for (LivingEntity e : entityTargetsTemp) {
                if (this.entity.distanceTo(e) <= 5.2) {
                    entityTargets.add(e);
                }
            }
        } else {
            // Calculate entity list for when shape is line (not cone)
            Vec3d lengthVector = forwardDirection.multiply(10);
            Vec3d heightVector = upDirection.multiply(2); //probably problem
            Vec3d widthVector = sideDirection.multiply(2);
            Vec3d originPoint = origin.subtract(widthVector.multiply(0.5)).subtract(heightVector.multiply(0.5));
            box = new Box(originPoint, heightVector, widthVector, lengthVector);
            entityTargets = box.getOtherLivingEntities(entity);
        }

        // ToDo DEBUG_START

        Vec3d leftDownPoint = box.getOrigin();
        Vec3d leftUpPoint = leftDownPoint.add(box.getHeight());
        Vec3d rightUpPoint = leftUpPoint.add(box.getWidth());
        Vec3d rightDownPoint = leftDownPoint.add(box.getWidth());
        Vec3d leftDownFarPoint = leftDownPoint.add(box.getLength());
        Vec3d leftUpFarPoint = leftDownFarPoint.add(box.getHeight());
        Vec3d rightUpFarPoint = leftUpFarPoint.add((box.getWidth()));
        Vec3d rightDownFarPoint = rightUpFarPoint.subtract(box.getHeight());

        this.entity.getWorld().addImportantParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, leftDownPoint.getX(), leftDownPoint.getY(), leftDownPoint.getZ(), 0, 0, 0);
        this.entity.getWorld().addImportantParticle(ModParticles.PINK_SMOKE, leftUpPoint.getX(), leftUpPoint.getY(), leftUpPoint.getZ(), 0, 0, 0);
        this.entity.getWorld().addImportantParticle(ModParticles.PINK_SMOKE, rightDownPoint.getX(), rightDownPoint.getY(), rightDownPoint.getZ(), 0, 0, 0);
        this.entity.getWorld().addImportantParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, rightUpPoint.getX(), rightUpPoint.getY(), rightUpPoint.getZ(), 0, 0, 0);
        this.entity.getWorld().addImportantParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, leftDownFarPoint.getX(), leftDownFarPoint.getY(), leftDownFarPoint.getZ(), 0, 0, 0);
        this.entity.getWorld().addImportantParticle(ModParticles.PINK_SMOKE, leftUpFarPoint.getX(), leftUpFarPoint.getY(), leftUpFarPoint.getZ(), 0, 0, 0);
        this.entity.getWorld().addImportantParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, rightUpFarPoint.getX(), rightUpFarPoint.getY(), rightUpFarPoint.getZ(), 0, 0, 0);
        this.entity.getWorld().addImportantParticle(ModParticles.PINK_SMOKE, rightDownFarPoint.getX(), rightDownFarPoint.getY(), rightDownFarPoint.getZ(), 0, 0, 0);

        System.out.println("origin: " + origin);
        System.out.println("forward direction: " + forwardDirection);
        System.out.println("up direction: " + upDirection);
        System.out.println("side direction: " + sideDirection);
        System.out.println("box: " + box);
        System.out.println("box heightPoint: " + leftUpPoint);
        System.out.println("box widthPoint: " + rightDownPoint);
        System.out.println("box lengthPoint: " + leftDownFarPoint);
        System.out.println("targets: " + entityTargets);

        // ToDo DEBUG_END
        return entityTargets;
    }

    private Vec3d getRotationVec(float pitch, float yaw) {
        float f = pitch * ((float)Math.PI / 180);
        float g = -yaw * ((float)Math.PI / 180);
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return new Vec3d(i * j, -k, h * j);
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
