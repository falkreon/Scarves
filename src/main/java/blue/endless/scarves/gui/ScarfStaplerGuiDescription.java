package blue.endless.scarves.gui;

import blue.endless.scarves.ScarfStaplerBlockEntity;
import blue.endless.scarves.ScarvesBlocks;
import blue.endless.scarves.ScarvesItems;
import blue.endless.scarves.api.FabricSquareRegistry;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;

public class ScarfStaplerGuiDescription  extends SyncedGuiDescription{
	public ScarfStaplerGuiDescription(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(ScarvesBlocks.SCARF_STAPLER_SCREEN_HANDLER, syncId, playerInventory, getBlockInventory(context, 3), getBlockPropertyDelegate(context));
		
		
		WGridPanel root = new WGridPanel();
		setRootPanel(root);

		root.setSize(100, 100);
		root.setInsets(Insets.ROOT_PANEL);

		WItemSlot itemSlot = WItemSlot.of(blockInventory, ScarfStaplerBlockEntity.SCARF_SLOT);
		itemSlot.setFilter(it->it.isOf(ScarvesItems.SCARF));
		root.add(itemSlot, 4, 2);
		
		WItemSlot leftSlot = WItemSlot.of(blockInventory, ScarfStaplerBlockEntity.LEFT_SLOT);
		leftSlot.setFilter(FabricSquareRegistry::isFabricSquare);
		root.add(leftSlot, 2, 2);
		
		WItemSlot rightSlot = WItemSlot.of(blockInventory, ScarfStaplerBlockEntity.RIGHT_SLOT);
		rightSlot.setFilter(FabricSquareRegistry::isFabricSquare);
		root.add(rightSlot, 6, 2);

		root.add(this.createPlayerInventoryPanel(), 0, 4);

		root.validate(this);
	}
}
