package com.github.ZombifiedPatato.pumpkin_patch.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.CraftingScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(CraftingScreenHandler.class)
public class CraftingScreenHandlerMixin {

	@ModifyVariable(method = "updateResult", at = @At("STORE"), ordinal = 0)
	private static ItemStack craftedItem(ItemStack itemStack) {
		return ItemStack.EMPTY;
	}
}
