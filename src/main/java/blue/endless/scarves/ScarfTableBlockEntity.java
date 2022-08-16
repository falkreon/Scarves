package blue.endless.scarves;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class ScarfTableBlockEntity extends BlockEntity implements ImplementedInventory, Nameable {
	private Text customName;
	private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(10, ItemStack.EMPTY);
	private int[] tints = {
			0xFF_FFFFFF, 0xFF_FFFFFF, 0xFF_FFFFFF, 0xFF_FFFFFF,
			0xFF_FFFFFF, 0xFF_FFFFFF, 0xFF_FFFFFF, 0xFF_FFFFFF
	};
	
	public static final int SLOT_INPUT = 8;
	public static final int SLOT_OUTPUT = 9;

	public ScarfTableBlockEntity(BlockPos pos, BlockState state) {
		super(ScarvesBlocks.SCARF_TABLE_ENTITY, pos, state);
	}

	@Override
	public DefaultedList<ItemStack> getItems() {
		return inventory;
	}
	
	@Override
	public void readNbt(NbtCompound nbt) {
		Inventories.readNbt(nbt, inventory);
		tints = nbt.getIntArray("Tints");
		super.readNbt(nbt);
	}
	
	@Override
	protected void writeNbt(NbtCompound nbt) {
		Inventories.writeNbt(nbt, inventory);
		nbt.putIntArray("Tints", tints);
		super.writeNbt(nbt);
	}
	
	public void setCustomName(Text name) {
		this.customName = name;
	}
	
	@Override
	public Text getName() {
		return customName;
	}
	
	
}
