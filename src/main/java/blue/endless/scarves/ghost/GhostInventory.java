package blue.endless.scarves.ghost;

import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public interface GhostInventory {
	public static final ImplementedGhostInventory DUMMY = DefaultedList::of;
	
	public int getGhostInventorySize();
	public ItemStack getGhostItem(int slot);
	public void setGhostItem(int slot, ItemStack stack);
	public void markDirty();
	
	public static GhostInventory ofSize(int size) {
		return new Impl(size);
	}
	
	public static class Impl implements ImplementedGhostInventory {
		private final DefaultedList<ItemStack> data;
		
		public Impl(int size) {
			data = DefaultedList.ofSize(size, ItemStack.EMPTY);
		}
		
		@Override
		public DefaultedList<ItemStack> getGhostItems() {
			return data;
		}
		
	}
}
