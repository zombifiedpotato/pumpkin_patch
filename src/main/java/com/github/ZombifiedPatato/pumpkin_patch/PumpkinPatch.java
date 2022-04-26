package com.github.ZombifiedPatato.pumpkin_patch;

import com.github.ZombifiedPatato.pumpkin_patch.block.ModBlocks;
import com.github.ZombifiedPatato.pumpkin_patch.entity.ModEntities;
import com.github.ZombifiedPatato.pumpkin_patch.item.ModItems;
import net.fabricmc.api.ModInitializer;
import com.github.ZombifiedPatato.pumpkin_patch.particle.ModParticles;
import net.kyrptonaught.customportalapi.api.CustomPortalBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class PumpkinPatch implements ModInitializer {
	public static final String MOD_ID = "pumpkin_patch";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModBlocks.registerBlocks();
		ModEntities.registerModEntities();
		ModParticles.registerModParticles();
		registerPortals();
	}

	private void registerPortals() {
		CustomPortalBuilder.beginPortal()
				.frameBlock(Blocks.AMETHYST_BLOCK)
				.destDimID(new Identifier("pumpkin_patch", "explorer_world"))
				.tintColor(255, 246, 168)
				.registerPortal();

	}
}
