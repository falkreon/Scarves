package blue.endless.scarves.integration;

import dev.emi.emi.api.EmiDragDropHandler;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.minecraft.item.ItemStack;

import java.util.List;

import blue.endless.scarves.client.ScarfTableScreen;
import blue.endless.scarves.ghost.GhostInventoryNetworking;
import blue.endless.scarves.ghost.WGhostSlot;

public class EmiIntegration implements EmiPlugin {
	
	@Override
	public void register(EmiRegistry registry) {
		registry.addDragDropHandler(ScarfTableScreen.class, new GhostItemDragDropHandler());
	}
	
	public static class GhostItemDragDropHandler implements EmiDragDropHandler<ScarfTableScreen> {

		@Override
		public boolean dropStack(ScarfTableScreen screen, EmiIngredient stack, int x, int y) {
			
			ItemStack targetItem = convertEmiIngredient(stack);
			if (targetItem.isEmpty()) return false;
			
			int hitX = x - screen.getX();
			int hitY = y - screen.getY();
			
			WWidget widget = screen.getScreenHandler().getRootPanel().hit(hitX, hitY);
			if (widget instanceof WGhostSlot ghostSlot) {
				if (!ghostSlot.getFilter().test(targetItem)) return false;
				int index = ghostSlot.getIndex();
				
				screen.getScreenHandler().getGhostInventory().setGhostItem(index, targetItem);
				GhostInventoryNetworking.sendGhostItemToServer(index, targetItem);
				
				return true;
			}
			
			return false;
		}
		
	}
	
	public static ItemStack convertEmiIngredient(EmiIngredient ingredient) {
		if (ingredient.isEmpty()) return ItemStack.EMPTY;
		
		List<EmiStack> stacks = ingredient.getEmiStacks();
		if (stacks.isEmpty()) return ItemStack.EMPTY;
		
		EmiStack stack = stacks.get(0);
		return stack.getItemStack();
	}
}
