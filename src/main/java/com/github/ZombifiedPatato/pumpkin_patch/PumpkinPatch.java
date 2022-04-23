package com.github.ZombifiedPatato.pumpkin_patch;

import com.github.ZombifiedPatato.pumpkin_patch.block.ModBlocks;
import com.github.ZombifiedPatato.pumpkin_patch.entity.ModEntities;
import com.github.ZombifiedPatato.pumpkin_patch.item.ModItems;
import net.fabricmc.api.ModInitializer;
import com.github.ZombifiedPatato.pumpkin_patch.particle.ModParticles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PumpkinPatch implements ModInitializer {
	//gamerule dolimitedcrafting for blocking crafting recipes
	public static final String MOD_ID = "pumpkin_patch";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModBlocks.registerBlocks();
		ModEntities.registerModEntities();
		ModParticles.registerModParticles();
	}
}
