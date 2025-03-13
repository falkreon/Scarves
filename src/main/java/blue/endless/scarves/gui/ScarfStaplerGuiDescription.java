package blue.endless.scarves.gui;

import java.util.Map;

import blue.endless.scarves.ScarfStaplerBlockEntity;
import blue.endless.scarves.ScarvesBlocks;
import blue.endless.scarves.ScarvesItems;
import blue.endless.scarves.ScarvesMod;
import blue.endless.scarves.api.FabricSquareRegistry;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.networking.NetworkSide;
import io.github.cottonmc.cotton.gui.networking.ScreenNetworking;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.cottonmc.cotton.gui.widget.icon.TextureIcon;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ScarfStaplerGuiDescription  extends SyncedGuiDescription{
	public static final Identifier SCARF_SLOT_ICON = Identifier.of(ScarvesMod.MODID, "textures/gui/slots/scarf.png");
	public static final Identifier LEFT_SCARF_ICON = Identifier.of(ScarvesMod.MODID, "textures/gui/slots/scarf_l.png");
	public static final Identifier RIGHT_SCARF_ICON = Identifier.of(ScarvesMod.MODID, "textures/gui/slots/scarf_r.png");
	public static final Identifier SQUARE_SLOT_ICON = Identifier.of(ScarvesMod.MODID, "textures/gui/slots/square.png");
	public static final Identifier STAPLE_MESSAGE = Identifier.of(ScarvesMod.MODID, "ok_staple");
	
	public ScarfStaplerGuiDescription(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(ScarvesBlocks.SCARF_STAPLER_SCREEN_HANDLER, syncId, playerInventory, getBlockInventory(context, 3), getBlockPropertyDelegate(context));
		
		//Register the staple message
		ScreenNetworking.of(this, NetworkSide.SERVER).receive(STAPLE_MESSAGE, Identifier.CODEC, this::staple);
		
		WGridPanel root = new WGridPanel();
		setRootPanel(root);

		root.setSize(100, 100);
		root.setInsets(Insets.ROOT_PANEL);

		WItemSlot itemSlot = WItemSlot.of(blockInventory, ScarfStaplerBlockEntity.SCARF_SLOT);
		itemSlot.setInputFilter(it->it.isOf(ScarvesItems.SCARF));
		itemSlot.setIcon(new TextureIcon(SCARF_SLOT_ICON));
		root.add(itemSlot, 4, 1);
		
		WItemSlot leftSlot = WItemSlot.of(blockInventory, ScarfStaplerBlockEntity.LEFT_SLOT);
		leftSlot.setInputFilter(FabricSquareRegistry::canBeStapled);
		leftSlot.setIcon(new TextureIcon(SQUARE_SLOT_ICON));
		root.add(leftSlot, 2, 1);
		
		WButton stapleButton = new WButton(Text.translatable("gui.scarves.staple"));
		stapleButton.setOnClick(()->{
			ScreenNetworking.of(this, NetworkSide.CLIENT).send(STAPLE_MESSAGE, Identifier.CODEC, STAPLE_MESSAGE);
		});
		root.add(stapleButton, 2, 2, 5, 1);

		root.add(this.createPlayerInventoryPanel(), 0, 5);
		
		/*
		Map<String, Map<String, TrinketInventory>> inventoryMap = TrinketsApi.getTrinketComponent(playerInventory.player).get().getInventory();
		Map<String, TrinketInventory> headGroup = inventoryMap.get("head");
		if (headGroup!=null) {
			TrinketInventory leftScarfInventory = headGroup.get("left_scarf");
			if (leftScarfInventory!=null && leftScarfInventory.size()>0) {
				WItemSlot playerScarfSlot = WItemSlot.of(leftScarfInventory, 0);
				playerScarfSlot.setInputFilter(it->it.isOf(ScarvesItems.SCARF));
				playerScarfSlot.setIcon(new TextureIcon(SCARF_SLOT_ICON));
				root.add(playerScarfSlot, 3, 4);
			}
			
			WAtlasSprite headImage = new WAtlasSprite(PlayerScreenHandler.EMPTY_HELMET_SLOT_TEXTURE);
			headImage.setSize(16, 16);
			root.add(headImage, 4, 4);
			
			TrinketInventory rightScarfInventory = headGroup.get("right_scarf");
			if (rightScarfInventory!=null && rightScarfInventory.size()>0) {
				WItemSlot playerScarfSlot = WItemSlot.of(rightScarfInventory, 0);
				playerScarfSlot.setInputFilter(it->it.isOf(ScarvesItems.SCARF));
				playerScarfSlot.setIcon(new TextureIcon(SCARF_SLOT_ICON));
				root.add(playerScarfSlot, 5, 4);
			}
		}*/
		addScarfSlots(root, playerInventory, 3, 4);

		root.validate(this);
	}
	
	public static void addScarfSlots(WGridPanel root, PlayerInventory playerInventory, int xofs, int yofs) {
		Map<String, Map<String, TrinketInventory>> inventoryMap = TrinketsApi.getTrinketComponent(playerInventory.player).get().getInventory();
		Map<String, TrinketInventory> headGroup = inventoryMap.get("head");
		if (headGroup!=null) {
			TrinketInventory leftScarfInventory = headGroup.get("left_scarf");
			if (leftScarfInventory!=null && leftScarfInventory.size()>0) {
				WItemSlot playerScarfSlot = WItemSlot.of(leftScarfInventory, 0);
				playerScarfSlot.setInputFilter(it->it.isOf(ScarvesItems.SCARF));
				playerScarfSlot.setIcon(new TextureIcon(LEFT_SCARF_ICON));
				root.add(playerScarfSlot, xofs, yofs);
			}
			
			WAtlasSprite headImage = new WAtlasSprite(PlayerScreenHandler.EMPTY_HELMET_SLOT_TEXTURE);
			headImage.setSize(16, 16);
			root.add(headImage, xofs+1, yofs);
			
			TrinketInventory rightScarfInventory = headGroup.get("right_scarf");
			if (rightScarfInventory!=null && rightScarfInventory.size()>0) {
				WItemSlot playerScarfSlot = WItemSlot.of(rightScarfInventory, 0);
				playerScarfSlot.setInputFilter(it->it.isOf(ScarvesItems.SCARF));
				playerScarfSlot.setIcon(new TextureIcon(RIGHT_SCARF_ICON));
				root.add(playerScarfSlot, xofs+2, yofs);
			}
		}
	}
	
	public void staple(Identifier id) {
		if (this.blockInventory instanceof ScarfStaplerBlockEntity entity) {
			entity.staple();
		}
	}
}
