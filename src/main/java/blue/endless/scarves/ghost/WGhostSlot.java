package blue.endless.scarves.ghost;

import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

/**
 * Same as a single-item WItemSlot, except that any time non-air itemstack is placed there, it's ghosted in.
 */
public class WGhostSlot extends WWidget {
	public static final int FRAME_COLOR = 0xb8_3f84f2;
	public static final int PANEL_COLOR = 0x4c_000000;
	
	protected GhostInventory inventory;
	protected int index;
	protected Predicate<ItemStack> filter = it -> true;
	
	public WGhostSlot(GhostInventory inventory, int index) {
		this.inventory = inventory;
		this.index = index;
	}
	
	@Override
	public boolean canResize() {
		return true;
	}
	
	public void setFilter(Predicate<ItemStack> filter) {
		this.filter = filter;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	public @NotNull Predicate<ItemStack> getFilter() {
		return filter;
	}
	
	@Override
	public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
		//ScreenDrawing.drawBeveledPanel(context, x-1, y-1, getWidth()+2, getHeight()+2, TOPLEFT_COLOR, 0x00_000000, BOTTOMRIGHT_COLOR);
		RenderSystem.enableDepthTest();
		context.drawItemWithoutEntity(inventory.getGhostItem(index), x + getWidth() / 2 - 8, y + getHeight() / 2 - 8);
		ScreenDrawing.coloredRect(context, x, y, getWidth()-1, getHeight()-1, PANEL_COLOR);
		
		ScreenDrawing.coloredRect(context, x,             y,              width - 1, 1,       FRAME_COLOR); //Top
		ScreenDrawing.coloredRect(context, x - 1,         y,              1,         height,  FRAME_COLOR); //Left
		ScreenDrawing.coloredRect(context, x + width - 1, y,              1,         height,  FRAME_COLOR); //Right
		ScreenDrawing.coloredRect(context, x,             y + height - 1, width - 1, 1,       FRAME_COLOR); //Bottom
	}
	
	@Override
	public InputResult onClick(int x, int y, int button) {
		if (button == 1) {
			inventory.setGhostItem(index, ItemStack.EMPTY);
			GhostInventoryNetworking.sendGhostItemToServer(index, ItemStack.EMPTY);
			return InputResult.PROCESSED;
		}
		
		if (this.getHost() instanceof ScreenHandler handler) {
			ItemStack cursorStack = handler.getCursorStack().copy();
			if (cursorStack.isEmpty() || !filter.test(cursorStack)) return InputResult.PROCESSED;
			
			inventory.setGhostItem(index, cursorStack);
			GhostInventoryNetworking.sendGhostItemToServer(index, cursorStack);
			
			return InputResult.PROCESSED;
		}
		
		return InputResult.IGNORED;
	}
}
