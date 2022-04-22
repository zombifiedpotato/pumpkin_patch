package com.github.ZombifiedPatato.pumpkin_patch.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import com.github.ZombifiedPatato.pumpkin_patch.PumpkinPatch;
import com.github.ZombifiedPatato.pumpkin_patch.entity.custom.DwarvenDynamiteEntity;
import com.github.ZombifiedPatato.pumpkin_patch.entity.custom.FairyPowderEntity;

public class ModEntities {

    public static final EntityType<FairyPowderEntity> FAIRY_POWDER= Registry.register(
            Registry.ENTITY_TYPE, new Identifier(PumpkinPatch.MOD_ID, "fairy_powder"),
            FabricEntityTypeBuilder.<FairyPowderEntity>create(SpawnGroup.MISC, FairyPowderEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                    .build()
    );

    public static final EntityType<DwarvenDynamiteEntity> DWARVEN_DYNAMITE = Registry.register(
            Registry.ENTITY_TYPE, new Identifier(PumpkinPatch.MOD_ID, "dwarven_dynamite"),
            FabricEntityTypeBuilder.<DwarvenDynamiteEntity>create(SpawnGroup.MISC, DwarvenDynamiteEntity::new)
                    .dimensions(EntityDimensions.fixed(0.98f, 0.98f))
                    .build()
    );


    public static void registerModEntities() {
        System.out.println("Registering Mod Entities for " + PumpkinPatch.MOD_ID);
    }

}
