package blue.endless.scarves.client;

import blue.endless.scarves.gui.ScarfTableGuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class ScarfTableScreen extends CottonInventoryScreen<ScarfTableGuiDescription> {

	public ScarfTableScreen(ScarfTableGuiDescription description, PlayerInventory inventory, Text title) {
		super(description, inventory, title);
	}
	
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
}
