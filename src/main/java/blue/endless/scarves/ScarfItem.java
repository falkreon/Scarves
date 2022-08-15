package blue.endless.scarves;

import dev.emi.trinkets.api.TrinketItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Rarity;

public class ScarfItem extends TrinketItem {

	public ScarfItem() {
		super(new FabricItemSettings().rarity(Rarity.UNCOMMON).group(ItemGroup.TOOLS));
	}

}
