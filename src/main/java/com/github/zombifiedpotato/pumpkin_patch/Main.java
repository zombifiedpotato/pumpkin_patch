package com.github.zombifiedpotato.pumpkin_patch;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ModInitializer {

    public static final String MOD_ID = "pumpkin_patch";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info(MOD_ID + " is initialising!");
    }
}
