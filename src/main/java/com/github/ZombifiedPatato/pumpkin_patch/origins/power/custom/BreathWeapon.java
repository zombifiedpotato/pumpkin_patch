package com.github.ZombifiedPatato.pumpkin_patch.origins.power.custom;

import com.github.ZombifiedPatato.pumpkin_patch.PumpkinPatch;
import com.github.ZombifiedPatato.pumpkin_patch.particle.ModParticles;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.power.CooldownPower;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.apace100.origins.data.OriginsDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.LinkedList;
import java.util.List;

public class BreathWeapon extends CooldownPower implements Active {

    private List<StatusEffectInstance> effects = new LinkedList<>();
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

    public static PowerFactory createFactory() {
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

        List<LivingEntity> entityTargets = new LinkedList<>();
        Vec3d origin = this.entity.getEyePos();
        Vec3d direction = this.entity.getRotationVec(1);
        Vec3d target;
        Vec3d targetPointBox;
        Vec3d originPointBox;
        Box box;
        if (isCone) {
            // Calculate entity list for when shape is cone
            target = origin.add(direction.multiply(5));
            targetPointBox = target;
            originPointBox = origin;
            box = new Box(targetPointBox, originPointBox);
        } else {
            // Calculate entity list for when shape is line (not cone)
            target = origin.add(direction.multiply(10));
            // ToDo need to get a vector with min values and max values, not just any values as it will result in invalid box
            targetPointBox = target.add(new Vec3d(-direction.getY(), direction.getX(), 0).multiply(2));
            originPointBox = origin.subtract(new Vec3d(-direction.getY(), direction.getX(), 0).multiply(2));
            box = new Box(targetPointBox, originPointBox);
        }

        // ToDo DEBUG_START
        this.entity.getWorld().addImportantParticle(ModParticles.PINK_SMOKE, origin.getX(), origin.getY(), origin.getZ(), 0, 0, 0);
        this.entity.getWorld().addImportantParticle(ModParticles.PINK_SMOKE, direction.getX(), direction.getY(), direction.getZ(), 0, 0, 0);
        this.entity.getWorld().addImportantParticle(ModParticles.PINK_SMOKE, target.getX(), target.getY(), target.getZ(), 0, 0, 0);
        this.entity.getWorld().addImportantParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, targetPointBox.getX(), targetPointBox.getY(), targetPointBox.getZ(), 0, 0, 0);
        this.entity.getWorld().addImportantParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, originPointBox.getX(), originPointBox.getY(), originPointBox.getZ(), 0, 0, 0);
        this.entity.getWorld().addImportantParticle(ParticleTypes.ANGRY_VILLAGER, box.getCenter().getX(), box.getCenter().getY(), box.getCenter().getZ(),0,0,0);
        this.entity.getWorld().addImportantParticle(ParticleTypes.ANGRY_VILLAGER, box.maxX, box.maxY, box.maxZ,0,0,0);
        this.entity.getWorld().addImportantParticle(ParticleTypes.ANGRY_VILLAGER, box.minX, box.minY, box.minZ,0,0,0);
        System.out.println("origin: " + origin);
        System.out.println("direction: " + direction);
        System.out.println("target: " + target);
        System.out.println("originPointBox: " + originPointBox);
        System.out.println("targetPointBox: " + targetPointBox);
        System.out.println("box: " + box);

        // ToDo DEBUG_END

        // ToDo check distance from this.entity to all entities in list
        List<Entity> entities = this.entity.getEntityWorld().getOtherEntities(this.entity, box);
        // filter out all nonliving entities (can be done with stream probably)
        for (Entity entity1 : entities) {
            if (entity1 instanceof LivingEntity livingEntity) {
                entityTargets.add(livingEntity);
            }
        }
        return entityTargets;
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
