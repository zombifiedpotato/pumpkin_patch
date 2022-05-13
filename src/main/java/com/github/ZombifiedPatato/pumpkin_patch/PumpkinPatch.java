package com.github.ZombifiedPatato.pumpkin_patch;

import com.github.ZombifiedPatato.pumpkin_patch.block.ModBlocks;
import com.github.ZombifiedPatato.pumpkin_patch.effect.ModEffects;
import com.github.ZombifiedPatato.pumpkin_patch.entity.ModEntities;
import com.github.ZombifiedPatato.pumpkin_patch.item.ModItems;
import com.github.ZombifiedPatato.pumpkin_patch.origins.condition.ModConditions;
import com.github.ZombifiedPatato.pumpkin_patch.origins.power.ModPowers;
import net.fabricmc.api.ModInitializer;
import com.github.ZombifiedPatato.pumpkin_patch.particle.ModParticles;
import net.kyrptonaught.customportalapi.api.CustomPortalBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
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
		ModConditions.registerModConditions();
		ModPowers.registerPowers();
		ModEffects.registerEffects();
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
