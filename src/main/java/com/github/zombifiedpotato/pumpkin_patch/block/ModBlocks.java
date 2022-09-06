package com.github.zombifiedpotato.pumpkin_patch.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import com.github.zombifiedpotato.pumpkin_patch.PumpkinPatch;
import com.github.zombifiedpotato.pumpkin_patch.block.custom.DwarvenDynamiteBlock;
import com.github.zombifiedpotato.pumpkin_patch.item.ModItemGroups;


public class ModBlocks {

    public static final Block DWARVEN_DYNAMITE_BLOCK = registerBlock("dwarven_dynamite_block",
            new DwarvenDynamiteBlock(FabricBlockSettings.of(Material.STONE).hardness(3f)), ModItemGroups.PUMPKIN_PATCH);

    private static Block registerBlock(String name, Block block, ItemGroup group) {
        registerBlockItem(name, block, group);
        return Registry.register(Registry.BLOCK, new Identifier(PumpkinPatch.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block, ItemGroup group) {
        return Registry.register(Registry.ITEM, new Identifier(PumpkinPatch.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings().group(group)));
    }

    public static void registerBlocks() {
        System.out.println("Registering Mod Blocks for " + PumpkinPatch.MOD_ID);
    }
}
