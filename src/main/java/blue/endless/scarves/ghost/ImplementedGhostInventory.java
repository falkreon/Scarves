package blue.endless.scarves.ghost;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;

/**
 * Like ImplementedInventory, but for ghost items
 */
@FunctionalInterface
public interface ImplementedGhostInventory extends GhostInventory {
	public DefaultedList<ItemStack> getGhostItems();
	
	public default void writeGhostItems(NbtCompound nbt) {
		DefaultedList<ItemStack> ghostItems = getGhostItems();
		
		NbtList nbtList = new NbtList();
		for (int i = 0; i < ghostItems.size(); ++i) {
			ItemStack itemStack = ghostItems.get(i);
			if (itemStack.isEmpty()) continue;
			NbtCompound nbtCompound = new NbtCompound();
			nbtCompound.putByte("Slot", (byte)i);
			itemStack.writeNbt(nbtCompound);
			nbtList.add(nbtCompound);
		}
		nbt.put("GhostItems", nbtList);
	}
	
	public default void readGhostItems(NbtCompound nbt) {
		DefaultedList<ItemStack> ghostItems = getGhostItems();
		
		NbtList nbtList = nbt.getList("GhostItems", NbtElement.COMPOUND_TYPE);
		for (int i = 0; i < nbtList.size(); ++i) {
			NbtCompound nbtCompound = nbtList.getCompound(i);
			int j = nbtCompound.getByte("Slot") & 0xFF;
			if (j < 0 || j >= ghostItems.size()) continue;
			ghostItems.set(j, ItemStack.fromNbt(nbtCompound));
		}
	}
	
	@Override
	default int getGhostInventorySize() {
		return getGhostItems().size();
	}
	
	@Override
	default ItemStack getGhostItem(int index) {
		return getGhostItems().get(index);
	}
	
	@Override
	default void setGhostItem(int slot, ItemStack stack) {
		if (slot >= getGhostItems().size()) return;
		getGhostItems().set(slot, stack);
	}
	
	public default void syncGhostItems(ServerPlayerEntity player) {
		if (getGhostInventorySize() <= 0) return;
		for(int i=0; i<getGhostInventorySize(); i++) {
			GhostInventoryNetworking.sendGhostItemToClient(player, i, getGhostItem(i));
		}
	}
	
	@Override
	default void markDirty() {}
}
