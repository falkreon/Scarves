package blue.endless.scarves;

import java.util.List;

import blue.endless.scarves.api.FabricSquare;
import blue.endless.scarves.api.FabricSquareRegistry;
import blue.endless.scarves.ghost.GhostInventory;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.math.Vec2f;

public class WScarfPreview extends WWidget {
	protected int panelSize = 24;
	protected int maxSquares = 8;
	
	//Used if this preview is of a GhostInventory. Takes precedence.
	private GhostInventory inventory = null;
	
	//Used if this preview is of the ScarfData on an ItemStack. Left scarf takes precedence if there are two.
	private Inventory exemplarInventory = null;
	private int exemplarSlot = 0;
	//private List<FabricSquare> cachedExemplar = null;
	
	private Axis axis = Axis.HORIZONTAL;
	
	public WScarfPreview(GhostInventory inventory, Axis axis) {
		this.inventory = inventory;
		this.axis = axis;
	}
	
	public WScarfPreview(Inventory inventory, int slot, Axis axis) {
		this.exemplarInventory = inventory;
		this.exemplarSlot = slot;
		this.axis = axis;
	}
	
	public void setPanelSize(int value) {
		this.panelSize = value;
	}
	
	public void setMaxSquares(int value) {
		this.maxSquares = value;
	}
	
	@Override
	public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
		int dx = (axis == Axis.HORIZONTAL) ? panelSize : 0;
		int dy = (axis == Axis.HORIZONTAL) ? 0 : panelSize;
		
		int ix = this.x + x;
		int iy = this.y + y;
		
		if (inventory != null) {
			int squares = Math.min(maxSquares, inventory.getGhostInventorySize());
			
			// Draw from ghost inventory
			for(int i=0; i<squares; i++) {
				ItemStack stack = inventory.getGhostItem(i);
				if (!stack.isEmpty()) {
					FabricSquare square = FabricSquareRegistry.forItem(stack);
					if (square != null) {
						paintSquare(context, ix, iy, panelSize, panelSize, square);
					}
				}
				
				ix += dx;
				iy += dy;
			}
			
			
		} else if (exemplarInventory != null) {
			ItemStack exemplar = exemplarInventory.getStack(exemplarSlot);
			NbtList list = FabricSquareRegistry.getStaplerData(exemplar);
			
			int squares = Math.min(maxSquares, list.size());
			for(int i=0; i<squares; i++) {
				NbtCompound squareNbt = list.getCompound(i);
				FabricSquare square = FabricSquare.fromCompound(squareNbt);
				paintSquare(context, ix, iy, panelSize, panelSize, square);
				
				ix += dx;
				iy += dy;
			}
		}
	}
	
	@Environment(EnvType.CLIENT)
	public void paintSquare(DrawContext context, int x, int y, int width, int height, FabricSquare square) {
		AbstractTexture tex = MinecraftClient.getInstance().getTextureManager().getTexture(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
		if (tex instanceof SpriteAtlasTexture atlas) {
			Sprite sprite = atlas.getSprite(square.id());
			float uPx = (sprite.getMaxU() - sprite.getMinU()) / 16f;
			float vPx = (sprite.getMaxV() - sprite.getMinV()) / 16f;
			
			float uofs = uPx * square.xofs();
			float vofs = vPx * square.yofs();
			
			float minU = sprite.getMinU() + uofs;
			float minV = sprite.getMinV() + vofs;
			float maxU = minU + (uPx * 8);
			float maxV = minV + (vPx * 8);
		
			ScreenDrawing.texturedRect(context, x, y, width, height, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, minU, minV, maxU, maxV, square.color());
		} else {
			ScarvesMod.LOGGER.error("Block Atlas Texture isn't a block atlas");
		}
	}
	
}
