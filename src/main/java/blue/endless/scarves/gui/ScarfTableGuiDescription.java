package blue.endless.scarves.gui;

import blue.endless.scarves.ScarfStaplerBlockEntity;
import blue.endless.scarves.ScarfTableBlockEntity;
import blue.endless.scarves.ScarvesBlocks;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;

public class ScarfTableGuiDescription extends SyncedGuiDescription {

	public ScarfTableGuiDescription(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(ScarvesBlocks.SCARF_TABLE_SCREEN_HANDLER, syncId, playerInventory, getBlockInventory(context, 10), getBlockPropertyDelegate(context, 8));
		
		
		WGridPanel root = new WGridPanel();
		setRootPanel(root);

		root.setSize(256, 256);
		root.setInsets(Insets.ROOT_PANEL);

		WItemSlot scarfSlot = WItemSlot.of(blockInventory, ScarfStaplerBlockEntity.SCARF_SLOT);
		root.add(scarfSlot, 4, 3);
		
		
		
		WItemSlot squareSlots = WItemSlot.of(blockInventory, 0, 8, 1);
		root.add(squareSlots, 1, 2);

		root.add(this.createPlayerInventoryPanel(), 0, 4);

		root.validate(this);
	}
	

}
