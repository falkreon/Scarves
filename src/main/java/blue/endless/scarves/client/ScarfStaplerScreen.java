package blue.endless.scarves.client;

import blue.endless.scarves.gui.ScarfStaplerGuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class ScarfStaplerScreen extends CottonInventoryScreen<ScarfStaplerGuiDescription> {

	public ScarfStaplerScreen(ScarfStaplerGuiDescription description, PlayerInventory inventory, Text title) {
		super(description, inventory, title);
	}

}
