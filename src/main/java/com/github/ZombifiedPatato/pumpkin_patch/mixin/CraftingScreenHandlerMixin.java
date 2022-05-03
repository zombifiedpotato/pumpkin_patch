package com.github.ZombifiedPatato.pumpkin_patch.mixin;

import com.github.ZombifiedPatato.pumpkin_patch.PumpkinPatch;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.origin.OriginLayers;
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
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(CraftingScreenHandler.class)
public class CraftingScreenHandlerMixin {

	@Inject(method = "updateResult", at = @At(value = "TAIL", shift = At.Shift.BY, by = -3), cancellable = true)
	private static void craftedItem(ScreenHandler handler, World world, PlayerEntity player, CraftingInventory craftingInventory, CraftingResultInventory resultInventory, CallbackInfo callbackInfo) {
		List<String> mi_origins = List.of("automaton", "dwarf");
		List<String> spectrum_origins = List.of("test_2");
		List<String> bewitchment_botania_origins = List.of("test_3");
		try {
			Optional<CraftingRecipe> optional = world.getServer().getRecipeManager().getFirstMatch(RecipeType.CRAFTING, craftingInventory, world);
			if (optional.isPresent()){
				String namespace = optional.get().getId().getNamespace();
				HashMap<OriginLayer,Origin> origins = Origin.get(player);
				String origin = origins.get(OriginLayers.getLayer(new Identifier(PumpkinPatch.MOD_ID, "races"))).getIdentifier().getPath();
				if (
						(namespace.equals("modern_industrialization") && !mi_origins.contains(origin)) ||
						(namespace.equals("spectrum") && !spectrum_origins.contains(origin)) ||
						((namespace.equals("bewitchment") || namespace.equals("botania")) && !bewitchment_botania_origins.contains(origin))
				){
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
