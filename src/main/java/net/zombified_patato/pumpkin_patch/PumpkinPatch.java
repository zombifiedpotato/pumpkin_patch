package net.zombified_patato.pumpkin_patch;

import net.fabricmc.api.ModInitializer;
import net.zombified_patato.pumpkin_patch.block.ModBlocks;
import net.zombified_patato.pumpkin_patch.entity.ModEntities;
import net.zombified_patato.pumpkin_patch.item.ModItems;
import net.zombified_patato.pumpkin_patch.particle.ModParticles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PumpkinPatch implements ModInitializer {

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
