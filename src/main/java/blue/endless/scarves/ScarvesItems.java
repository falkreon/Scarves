package blue.endless.scarves;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

public class ScarvesItems {
	public static ScarfItem SCARF;
	
	
	public static void register() {
		SCARF = register( new ScarfItem(), "scarf" );
		
		register(new BlockItem(ScarvesBlocks.SCARF_TABLE, new FabricItemSettings().rarity(Rarity.UNCOMMON)), ScarfTableBlock.ID);
	}
	
	private  static <T extends Item> T register(T item, String id) {
		Registry.register(Registry.ITEM, new Identifier(ScarvesMod.MODID, id), item);
		return item;
	}
}