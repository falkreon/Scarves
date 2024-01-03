package blue.endless.scarves.api;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import blue.endless.scarves.ScarvesMod;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;

public class FabricSquareRegistry {
	private static Map<ItemConvertible, FabricSquare> entries = new HashMap<>();
	
	static {
		/*
		
		
		register(Blocks.BEDROCK,         "minecraft:block/bedrock");
		register(Blocks.PRISMARINE,      "minecraft:block/prismarine");
		register(Blocks.PURPUR_BLOCK,    "minecraft:block/purpur_block");
		register(Blocks.SANDSTONE,       "minecraft:block/sandstone_top");
		register(Blocks.RED_SANDSTONE,   "minecraft:block/red_sandstone_top");
		register(Blocks.GLOWSTONE,       "minecraft:block/glowstone");*/
		//register(Blocks.REDSTONE_BLOCK,  new FabricSquare(new Identifier("minecraft:block/redstone_block"), 4, 4, 0xFF_FFFFFF, 0xFF_b50000, true));
		//register(Items.LAVA_BUCKET,      new FabricSquare(new Identifier("minecraft:block/lava_still"), 4, 4, 0xFF_FFFFFF, true));
		//register(Items.WATER_BUCKET,     new FabricSquare(new Identifier("minecraft:block/water_still"), 4, 4, 0xFF_4444FF, false));
	}
	
	/**
	 * Registers a block or item for use with the Scarf Stapler. Using a FabricSquare gives you full control over the
	 * visual characteristics of that part of the scarf.
	 * 
	 * <p>Note: If the item's visual characteristics depend on NBT, consider supplying a FabricSquare NBT key instead. If
	 * you're just registering a block and want to use the middle of its texture, use {@link #register(Block, Identifier)}
	 * instead.
	 * @param item the item to register
	 * @param square the fabric square
	 */
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
	
	/**
	 * Registers a block for use with the Scarf Stapler. The center 8x8 pixels will be used.
	 * @param block	The block to register
	 * @param texture The identifier of the texture to use. This is usually the same identifier as the texture reference in the blockmodel, e.g. "minecraft:block/glowstone"
	 */
	public static void register(Block block, Identifier texture) {
		if (block.getDefaultState().getLuminance()>=8) {
			entries.put(block, new FabricSquare(texture, 4, 4, 0xFF_FFFFFF, 0xFF_FFFFFF, true));
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
			return FabricSquare.fromCompound(tag);
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
	
	public static boolean canBeStapled(ItemStack stack) {
		if (isFabricSquare(stack)) return true;
		
		NbtCompound tag = stack.getNbt();
		if (tag==null) return false;
		
		NbtList leftScarfTag = tag.getList("LeftScarf", NbtElement.COMPOUND_TYPE);
		NbtList rightScarfTag = tag.getList("RightScarf", NbtElement.COMPOUND_TYPE);
			
		boolean hasLeftData = (leftScarfTag==null) ? false : leftScarfTag.size()>0;
		boolean hasRightData = (rightScarfTag==null) ? false : rightScarfTag.size()>0;
		
		return hasLeftData ^ hasRightData;
	}
	
	public static NbtList getStaplerData(ItemStack stack) {
		if (isFabricSquare(stack)) {
			NbtList result = new NbtList();
			result.add(forItem(stack).toCompound());
			return result;
		} else {
			NbtCompound tag = stack.getNbt();
			if (tag==null) return new NbtList();
			
			NbtList leftScarfTag = tag.getList("LeftScarf", NbtElement.COMPOUND_TYPE);
			NbtList rightScarfTag = tag.getList("RightScarf", NbtElement.COMPOUND_TYPE);
			
			boolean hasLeftData = (leftScarfTag==null) ? false : leftScarfTag.size()>0;
			boolean hasRightData = (rightScarfTag==null) ? false : rightScarfTag.size()>0;
			
			if (hasLeftData) {
				return leftScarfTag;
			} else if (hasRightData) {
				return rightScarfTag;
			} else {
				return new NbtList();
			}
		}
	}

	public static void logDump() {
		ScarvesMod.LOGGER.info("There are "+entries.size()+" entries in the FabricSquareRegistry:");
		entries.forEach((item, entry) -> {
			ScarvesMod.LOGGER.info(item + " -> " + entry);
		});
		
	}
}
