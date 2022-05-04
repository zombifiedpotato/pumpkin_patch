package com.github.ZombifiedPatato.pumpkin_patch.mixin;

import com.github.ZombifiedPatato.pumpkin_patch.interfaces.CraftingScreenHandlerInterface;
import com.github.ZombifiedPatato.pumpkin_patch.origins.power.custom.ModCraftingBlocker;
import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(CraftingScreenHandler.class)
public class CraftingScreenHandlerMixin implements CraftingScreenHandlerInterface {

	@Inject(method = "updateResult", at = @At(value = "TAIL", shift = At.Shift.BY, by = -3), cancellable = true)
	private static void craftedItem(ScreenHandler handler, World world, PlayerEntity player, CraftingInventory craftingInventory, CraftingResultInventory resultInventory, CallbackInfo callbackInfo) {
		try {
			Optional<CraftingRecipe> optional = world.getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craftingInventory, world);
			if (optional.isPresent()){
				String namespace = optional.get().getId().getNamespace();
				List<ModCraftingBlocker> powers = PowerHolderComponent.getPowers(player, ModCraftingBlocker.class);
				boolean craftable = false;
				for (ModCraftingBlocker power : powers) {
					if(power.isCraftable(namespace))
						craftable = true;
				}
				if (modids.contains(namespace) && !craftable){
					//not craftable
					ItemStack itemStack = ItemStack.EMPTY;
					ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
					resultInventory.setStack(0, itemStack);
					handler.setPreviousTrackedSlot(0, itemStack);
					serverPlayerEntity.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(handler.syncId, handler.nextRevision(), 0, itemStack));
					callbackInfo.cancel();
				}
			}
		}catch (Exception ignored) {}
	}
}
