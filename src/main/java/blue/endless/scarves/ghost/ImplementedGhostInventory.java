package blue.endless.scarves.ghost;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;

/**
 * Like ImplementedInventory, but for ghost items
 */
@FunctionalInterface
public interface ImplementedGhostInventory extends GhostInventory {
	public DefaultedList<ItemStack> getGhostItems();
	
	public default void writeGhostItems(NbtCompound nbt, WrapperLookup registryLookup) {
		DefaultedList<ItemStack> ghostItems = getGhostItems();
		
		NbtList nbtList = new NbtList();

		for (int i = 0; i < ghostItems.size(); i++) {
			ItemStack itemStack = ghostItems.get(i);
			if (!itemStack.isEmpty()) {
				NbtCompound nbtCompound = new NbtCompound();
				nbtCompound.putByte("Slot", (byte)i);
				nbtList.add(itemStack.encode(registryLookup, nbtCompound));
			}
		}
		
		nbt.put("GhostItems", nbtList);
	}
	
	public default void readGhostItems(NbtCompound nbt, WrapperLookup registryLookup) {
		NbtList nbtList = nbt.getList("GhostItems", NbtElement.COMPOUND_TYPE);
		DefaultedList<ItemStack> ghostItems = getGhostItems();
		
		for (int i = 0; i < nbtList.size(); i++) {
			NbtCompound nbtCompound = nbtList.getCompound(i);
			int j = nbtCompound.getByte("Slot") & 255;
			if (j >= 0 && j < ghostItems.size()) {
				ghostItems.set(j, (ItemStack)ItemStack.fromNbt(registryLookup, nbtCompound).orElse(ItemStack.EMPTY));
			}
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
