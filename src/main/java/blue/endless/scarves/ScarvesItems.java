package blue.endless.scarves;

import net.minecraft.block.Block;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Equipment;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ScarvesItems {
	public static BlockItem SCARF_STAPLER;
	public static BlockItem SCARF_TABLE;
	
	public static ScarfItem SCARF;
	
	
	public static void register() {
		SCARF = register( new ScarfItem(), ScarfItem.ID );
		
		SCARF_STAPLER = register(new HeadEquippableBlock(ScarvesBlocks.SCARF_STAPLER, new Item.Settings().rarity(Rarity.UNCOMMON)) , ScarfStaplerBlock.ID);
		SCARF_TABLE = register(new BlockItem(ScarvesBlocks.SCARF_TABLE, new Item.Settings().rarity(Rarity.UNCOMMON)), ScarfTableBlock.ID);
	}
	
	private  static <T extends Item> T register(T item, String id) {
		Registry.register(Registries.ITEM, Identifier.of(ScarvesMod.MODID, id), item);
		return item;
	}
	
	private static class HeadEquippableBlock extends BlockItem implements Equipment {

		public HeadEquippableBlock(Block block, Settings settings) {
			super(block, settings);
		}

		@Override
		public EquipmentSlot getSlotType() {
			return EquipmentSlot.HEAD;
		}
	}
}