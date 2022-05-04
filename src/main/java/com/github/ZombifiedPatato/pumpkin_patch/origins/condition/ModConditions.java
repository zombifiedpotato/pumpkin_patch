package com.github.ZombifiedPatato.pumpkin_patch.origins.condition;

import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ModConditions {
    public static final ConditionFactory<Entity> ENTITYTYPE_IN_RANGE = new ConditionFactory<>(
            new Identifier("pumpkin_patch", "entity_type_in_range"),
            new SerializableData().add("entity_types",SerializableDataType.list(SerializableDataTypes.ENTITY_TYPE)).add("radius", SerializableDataTypes.FLOAT),
            ((instance, entity) -> {
                float radius = instance.getFloat("radius");
                Box range = new Box(entity.getX() - radius, entity.getY() -radius, entity.getZ() -radius,
                        entity.getX() + radius, entity.getY()+radius, entity.getZ()+radius);
                List<EntityType> entityTypes = instance.get("entity_types");
                List<LivingEntity> livingEntityList = new ArrayList<>();
                for (EntityType e:entityTypes) {
                    Predicate<LivingEntity> livingEntityPredicate = n -> n.getType().equals(e);
                    livingEntityList.addAll(entity.getEntityWorld().getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class), range, livingEntityPredicate));
                }
                return !livingEntityList.isEmpty();
            }));

    public static void registerModConditions() {
        Registry.register(ApoliRegistries.ENTITY_CONDITION, ENTITYTYPE_IN_RANGE.getSerializerId(), ENTITYTYPE_IN_RANGE);
    }
}
