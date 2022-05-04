package com.github.ZombifiedPatato.pumpkin_patch.interfaces;

import java.util.HashSet;
import java.util.Set;


public interface CraftingScreenHandlerInterface {

    Set<String> modids = new HashSet<>();

    static void addModids (Set<String> newModids) {
        modids.addAll(newModids);
    }
}
