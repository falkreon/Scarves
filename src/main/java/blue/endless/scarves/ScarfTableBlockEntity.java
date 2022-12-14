package blue.endless.scarves;

import blue.endless.scarves.gui.ScarfTableGuiDescription;
import blue.endless.scarves.util.ArrayPropertyDelegate;
import blue.endless.scarves.util.ImplementedInventory;
import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class ScarfTableBlockEntity extends BlockEntity implements ImplementedInventory, Nameable, PropertyDelegateHolder, NamedScreenHandlerFactory {
	private Text customName;
	private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(10, ItemStack.EMPTY);
	private int[] tints = {
			0xFF_FFFFFF, 0xFF_FFFFFF, 0xFF_FFFFFF, 0xFF_FFFFFF,
			0xFF_FFFFFF, 0xFF_FFFFFF, 0xFF_FFFFFF, 0xFF_FFFFFF
	};
	private ArrayPropertyDelegate propertyDelegate = new ArrayPropertyDelegate(tints);
	
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

	@Override
	public PropertyDelegate getPropertyDelegate() {
		return propertyDelegate;
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
