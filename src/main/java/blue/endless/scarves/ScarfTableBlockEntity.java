package blue.endless.scarves;

import java.util.List;
import java.util.ArrayList;

import blue.endless.scarves.api.FabricSquare;
import blue.endless.scarves.api.RepeatType;
import blue.endless.scarves.api.ScarfDesign;
import blue.endless.scarves.ghost.ImplementedGhostInventory;
import blue.endless.scarves.gui.ScarfTableGuiDescription;
import blue.endless.scarves.util.ImplementedInventory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class ScarfTableBlockEntity extends BlockEntity implements ImplementedInventory, ImplementedGhostInventory, Nameable, NamedScreenHandlerFactory {
	private Text customName;
	private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
	private DefaultedList<ItemStack> ghostItems = DefaultedList.ofSize(8, ItemStack.EMPTY);
	
	public static final int SLOT_INPUT = 8;
	public static final int SLOT_OUTPUT = 9;

	public ScarfTableBlockEntity(BlockPos pos, BlockState state) {
		super(ScarvesBlocks.SCARF_TABLE_ENTITY, pos, state);
	}

	public List<FabricSquare> getGhostPattern(int patternLength) {
		List<FabricSquare> result = new ArrayList<>();
		int count = Math.max(patternLength, ghostItems.size());
		for(int i=0; i<count; i++) {
			ItemStack ghostItem = ghostItems.get(i);
			if (ghostItem.isEmpty()) continue;
			FabricSquare square = ghostItem.get(FabricSquare.COMPONENT);
			if (square != null) result.add(square);
		}
		
		return result;
	}
	
	public NbtList getAppliedPattern(int patternLength, int repetitions) {
		List<FabricSquare> pattern = getGhostPattern(patternLength);
		if (pattern.isEmpty()) return new NbtList();
		
		NbtList list = new NbtList();
		for(int i=0; i<repetitions; i++) {
			for(FabricSquare square : pattern) {
				list.add(square.toCompound());
			}
		}
		
		return list;
	}
	
	public void applyPattern(int patternLength, int repetitions) {
		ItemStack scarfStack = this.inventory.get(0);
		if (scarfStack.isEmpty()) return;
		
		List<FabricSquare> pattern = getGhostPattern(patternLength);
		ScarfDesign component = new ScarfDesign(RepeatType.FROM_START, repetitions, pattern);
		
		scarfStack.set(ScarfDesign.COMPONENT, component);
		
		this.setStack(0, scarfStack);
		this.markDirty();
	}
	
	@Override
	public DefaultedList<ItemStack> getItems() {
		return inventory;
	}
	
	@Override
	public DefaultedList<ItemStack> getGhostItems() {
		return ghostItems;
	}
	
	@Override
	protected void readNbt(NbtCompound nbt, WrapperLookup registryLookup) {
		Inventories.readNbt(nbt, inventory, registryLookup);
		readGhostItems(nbt, registryLookup);
		
		super.readNbt(nbt, registryLookup);
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt, WrapperLookup registryLookup) {
		Inventories.writeNbt(nbt, inventory, registryLookup);
		writeGhostItems(nbt, registryLookup);
		
		super.writeNbt(nbt, registryLookup);
	}
	
	public void setCustomName(Text name) {
		this.customName = name;
	}
	
	@Override
	public Text getName() {
		return customName;
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
		return new ScarfTableGuiDescription(syncId, playerInventory, ScreenHandlerContext.create(world, pos));
	}

	@Override
	public Text getDisplayName() {
		return Text.translatable(getCachedState().getBlock().getTranslationKey());
	}
}
