package blue.endless.scarves.gui;

import blue.endless.scarves.ScarfStaplerBlockEntity;
import blue.endless.scarves.ScarvesBlocks;
import blue.endless.scarves.ScarvesItems;
import blue.endless.scarves.ScarvesMod;
import blue.endless.scarves.api.FabricSquareRegistry;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.networking.NetworkSide;
import io.github.cottonmc.cotton.gui.networking.ScreenNetworking;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ScarfStaplerGuiDescription  extends SyncedGuiDescription{
	public static final Identifier STAPLE_MESSAGE = new Identifier(ScarvesMod.MODID, "ok_staple");
	
	public ScarfStaplerGuiDescription(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(ScarvesBlocks.SCARF_STAPLER_SCREEN_HANDLER, syncId, playerInventory, getBlockInventory(context, 3), getBlockPropertyDelegate(context));
		
		//Register the 
		ScreenNetworking.of(this, NetworkSide.SERVER).receive(STAPLE_MESSAGE, this::staple);
		
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
		
		WButton stapleButton = new WButton(Text.translatable("gui.scarves.staple"));
		stapleButton.setOnClick(()->{
			ScreenNetworking.of(this, NetworkSide.CLIENT).send(STAPLE_MESSAGE, buf->{});
		});
		root.add(stapleButton, 2, 3, 5, 1);

		root.add(this.createPlayerInventoryPanel(), 0, 5);

		root.validate(this);
	}
	
	public void staple(PacketByteBuf buf) {
		if (this.blockInventory instanceof ScarfStaplerBlockEntity entity) {
			entity.staple();
		}
	}
}
