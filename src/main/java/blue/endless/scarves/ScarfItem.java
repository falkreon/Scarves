package blue.endless.scarves;

import dev.emi.trinkets.api.TrinketItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.util.Rarity;

public class ScarfItem extends TrinketItem {
	public static final String ID = "scarf";
	
	public ScarfItem() {
		super(new FabricItemSettings().rarity(Rarity.UNCOMMON).group(ScarvesMod.ITEM_GROUP));
	}

}
