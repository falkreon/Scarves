package blue.endless.scarves.api;

import java.util.HashMap;
import java.util.Map;

import blue.endless.scarves.ScarvesMod;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;

public class FabricSquareRegistry {
	private static Map<Item, FabricSquare> entries = new HashMap<>();
	
	public static void init() {
		
		// TODO: Move staticdata lookup for fabric squares here.
		
		DefaultItemComponentEvents.MODIFY.register((ctx) -> {
			for(Map.Entry<Item, FabricSquare> entry : entries.entrySet()) {
				ctx.modify(entry.getKey().asItem(), (builder) -> {
					builder.add(FabricSquare.COMPONENT, entry.getValue());
				});
			}
		});
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
		entries.remove(item.asItem());
		entries.put(item.asItem(), square);
	}
	
	/**
	 * Registers a block for use with the Scarf Stapler. The center 8x8 pixels will be used.
	 * @param block	The block to register
	 * @param texture The identifier of the texture to use. This is usually the same identifier as the texture reference in the blockmodel, e.g. "minecraft:block/glowstone"
	 */
	/*
	public static void register(Block block, Identifier texture) {
		if (block.getDefaultState().getLuminance()>=8) {
			entries.put(block, new FabricSquare(texture, 4, 4, 0xFF_FFFFFF, 0xFF_FFFFFF, true));
		} else {
			entries.put(block, new FabricSquare(texture));
		}
	}
	
	public static void register(Block block, String texture) {
		register(block, Identifier.of(texture));
	}*/
	
	/**
	 * Gets the appearance of the scarf square that would result if you stitched this item or block into a scarf. Does
	 * not take into account NBT. If you can, use {@link #forItem(ItemStack)} instead.
	 */
	/*
	public static @Nullable FabricSquare forItemConvertible(ItemConvertible item) {
		if (item instanceof BlockItem blockItem) {
			Block block = blockItem.getBlock();
			return entries.get(block);
		} else {
			return entries.get(item);
		}
	}*/
	
	/*
	@Deprecated
	public static @Nullable NbtElement getLegacyTag(ItemStack stack, String tagName) {
		NbtComponent component = stack.get(DataComponentTypes.CUSTOM_DATA);
		if (component == null) return null;
		
		NbtCompound root = component.getNbt();
		if (root == null) return null;
		
		if (root.contains(tagName, NbtElement.COMPOUND_TYPE)) {
			return root.getCompound(tagName);
		} else {
			return null;
		}
	}
	
	@Deprecated
	public static @Nullable NbtCompound getSquareNbt(ItemStack stack) {
		NbtElement elem = getLegacyTag(stack, "FabricSquare");
		return (elem instanceof NbtCompound comp) ? comp : null;
	}
	
	@Deprecated
	public static @Nullable NbtList getLeftScarf(ItemStack stack) {
		NbtElement elem = getLegacyTag(stack, "LeftScarf");
		if (elem instanceof NbtList list) {
			if (list.getHeldType() == NbtElement.COMPOUND_TYPE) return list;
		}
		
		return null;
	}
	
	@Deprecated
	public static @Nullable NbtList getRightScarf(ItemStack stack) {
		NbtElement elem = getLegacyTag(stack, "RightScarf");
		if (elem instanceof NbtList list) {
			if (list.getHeldType() == NbtElement.COMPOUND_TYPE) return list;
		}
		
		return null;
	}*/
	
	/**
	 * Gets the appearance of the scarf square that would result if you stitched this item into a scarf.
	 */
	/*
	public static @Nullable FabricSquare forItem(ItemStack stack) {
		NbtCompound tag = getSquareNbt(stack);
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
	}*/
	
	/**
	 * Returns true if items from this stack can be stitched into a scarf
	 */
	/*
	public static boolean isFabricSquare(ItemStack stack) {
		NbtCompound tag = getSquareNbt(stack);
		if (tag!=null) return true;
		
		Item item = stack.getItem();
		if (item instanceof BlockItem blockItem) {
			Block block = blockItem.getBlock();
			return entries.containsKey(block);
		} else {
			return entries.containsKey(item);
		}
	}*/
	
	
	public static boolean canBeStapled(ItemStack stack) {
		return
			stack.get(ScarfDesign.COMPONENT) != null ||
			stack.get(FabricSquare.COMPONENT)!= null; 
	}
	
	/*
	public static NbtList getStaplerData(ItemStack stack) {
		if (isFabricSquare(stack)) {
			NbtList result = new NbtList();
			result.add(forItem(stack).toCompound());
			return result;
		} else {
			NbtList leftScarfTag = getLeftScarf(stack);
			NbtList rightScarfTag = getRightScarf(stack);
			
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
	}*/

	public static void logDump() {
		ScarvesMod.LOGGER.info("There are "+entries.size()+" entries in the FabricSquareRegistry:");
		entries.forEach((item, entry) -> {
			ScarvesMod.LOGGER.info(item + " -> " + entry);
		});
		
	}
}
