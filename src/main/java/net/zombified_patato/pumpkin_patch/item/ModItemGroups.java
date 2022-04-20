package net.zombified_patato.pumpkin_patch.item;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.zombified_patato.pumpkin_patch.PumpkinPatch;

public class ModItemGroups {
    public static final ItemGroup PUMPKIN_PATCH = FabricItemGroupBuilder.build(new Identifier(PumpkinPatch.MOD_ID, "pumpkin_patch"),
            () -> new ItemStack(ModItems.FAIRY_POWDER));
}
