package net.zombified_patato.pumpkin_patch.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.zombified_patato.pumpkin_patch.PumpkinPatch;
import net.zombified_patato.pumpkin_patch.item.custom.FairyPowderItem;

public class ModItems {

    public static final Item FAIRY_POWDER = registerItem("fairy_powder",
            new FairyPowderItem(new FabricItemSettings().group(ModItemGroups.PUMPKIN_PATCH).maxCount(16)));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(PumpkinPatch.MOD_ID, name), item);
    }

    public static void registerModItems() {
        System.out.println("Registering Mod Items for " + PumpkinPatch.MOD_ID);
    }

}
