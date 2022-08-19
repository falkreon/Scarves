package blue.endless.scarves.api;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;

public class FabricSquareRegistry {
	private static Map<ItemConvertible, FabricSquare> entries = new HashMap<>();
	
	static {
		register(Blocks.WHITE_WOOL,      "minecraft:white_wool");
		register(Blocks.ORANGE_WOOL,     "minecraft:orange_wool");
		register(Blocks.MAGENTA_WOOL,    "minecraft:magenta_wool");
		register(Blocks.LIGHT_BLUE_WOOL, "minecraft:light_blue_wool");
		register(Blocks.YELLOW_WOOL,     "minecraft:yellow_wool");
		register(Blocks.LIME_WOOL,       "minecraft:lime_wool");
		register(Blocks.PINK_WOOL,       "minecraft:pink_wool");
		register(Blocks.GRAY_WOOL,       "minecraft:gray_wool");
		register(Blocks.LIGHT_GRAY_WOOL, "minecraft:light_gray_wool");
		register(Blocks.CYAN_WOOL,       "minecraft:cyan_wool");
		register(Blocks.PURPLE_WOOL,     "minecraft:purple_wool");
		register(Blocks.BLUE_WOOL,       "minecraft:blue_wool");
		register(Blocks.BROWN_WOOL,      "minecraft:brown_wool");
		register(Blocks.GREEN_WOOL,      "minecraft:green_wool");
		register(Blocks.RED_WOOL,        "minecraft:red_wool");
		register(Blocks.BLACK_WOOL,      "minecraft:black_wool");
		
		register(Blocks.GLOWSTONE,       "minecraft:glowstone");
		register(Items.LAVA_BUCKET,      new FabricSquare(new Identifier("minecraft:lava"), 4, 4, 0xFF_FFFFFF, true));
		register(Items.WATER_BUCKET,     new FabricSquare("minecraft:water"));
	}
	
	public static void register(ItemConvertible item, FabricSquare square) {
		
		entries.remove(item);
		
		if (item instanceof BlockItem blockItem) {
			Block block = blockItem.getBlock();
			//entries.remove(block); //this is implied by put
			entries.put(block, square);
		} else if (item instanceof Block block) {
			//We've removed the block version, make sure to remove the item version if it snuck in
			entries.remove(block.asItem());
			entries.put(item, square);
		} else {
			//Regular, non-block item
			entries.put(item, square);
		}
	}
	
	public static void register(Block block, Identifier texture) {
		if (block.getDefaultState().getLuminance()>=8) {
			entries.put(block, new FabricSquare(texture, 4, 4, 0xFF_FFFFFF, true));
		} else {
			entries.put(block, new FabricSquare(texture));
		}
	}
	
	public static void register(Block block, String texture) {
		register(block, new Identifier(texture));
	}
	
	/**
	 * Gets the appearance of the scarf square that would result if you stitched this item or block into a scarf. Does
	 * not take into account NBT. If you can, use {@link #forItem(ItemStack)} instead.
	 */
	public static @Nullable FabricSquare forItemConvertible(ItemConvertible item) {
		if (item instanceof BlockItem blockItem) {
			Block block = blockItem.getBlock();
			return entries.get(block);
		} else {
			return entries.get(item);
		}
	}
	
	/**
	 * Gets the appearance of the scarf square that would result if you stitched this item into a scarf.
	 */
	public static @Nullable FabricSquare forItem(ItemStack stack) {
		NbtCompound tag = stack.getSubNbt("FabricSquare");
		if (tag!=null) {
			String id = tag.getString("Id");
			int xofs = (tag.contains("X", NbtElement.INT_TYPE)) ? tag.getInt("X") : 4;
			int yofs = (tag.contains("Y", NbtElement.INT_TYPE)) ? tag.getInt("Y") : 4;
			int color = (tag.contains("Color", NbtElement.INT_TYPE)) ? tag.getInt("Color") : 0xFF_FFFFFF;
			boolean emissive = tag.getBoolean("Emissive");
			
			return new FabricSquare(new Identifier(id), xofs, yofs, color, emissive);
		}
		
		Item item = stack.getItem();
		if (item instanceof BlockItem blockItem) {
			Block block = blockItem.getBlock();
			return entries.get(block);
		} else {
			return entries.get(item);
		}
	}
	
	/**
	 * Returns true if items from this stack can be stitched into a scarf
	 */
	public static boolean isFabricSquare(ItemStack stack) {
		NbtCompound tag = stack.getSubNbt("FabricSquare");
		if (tag!=null) return true;
		
		Item item = stack.getItem();
		if (item instanceof BlockItem blockItem) {
			Block block = blockItem.getBlock();
			return entries.containsKey(block);
		} else {
			return entries.containsKey(item);
		}
	}
}
